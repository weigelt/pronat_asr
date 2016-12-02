package edu.kit.ipd.parse.multiasr.asr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import edu.kit.ipd.parse.audio.AudioFormat;
import edu.kit.ipd.parse.audio.AudioFormatMatcher;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.multiasr.asr.spi.IASR;
import edu.kit.ipd.parse.multiasr.asr.spi.IPostProcessor;
import edu.kit.ipd.parse.revise.support.EmptyMap;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.job.FFmpegJob.State;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class MultiASR implements IMultiASR {
	Properties props;
	private static final String FFMPEG_PROP = "FFMPEG";
	private static final String FFPROBE_PROP = "FFPROBE";
	private static final Logger logger = LoggerFactory.getLogger(MultiASR.class);
	private final List<IASR> asrs = new ArrayList<>();
	private final List<IPostProcessor> pps = new ArrayList<>();
	private FFmpeg ffmpeg;
	private final FFprobe ffprobe;
	private final FFmpegExecutor ffmpegExecutor;
	private Path tempFolder;
	private final AudioFormatMatcher audioFormatMatcher = (source,
			target) -> (target.getBitrate() <= 0 || source.getBitrate() == target.getBitrate())
			&& (target.getChannels() <= 0 || source.getChannels() == target.getChannels())
			&& (target.getSamplingRate() <= 0 || source.getSamplingRate() == target.getSamplingRate())
			&& (Strings.isNullOrEmpty(target.getFormat()) || source.getFormat().equals(target.getFormat()))
			&& (Strings.isNullOrEmpty(target.getCodec()) || source.getCodec().equals(target.getCodec()));

			public MultiASR() {
				props = ConfigManager.getConfiguration(getClass());

				try {
					this.tempFolder = Files.createTempDirectory("MultiASR");

					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							try {
								FileUtils.deleteDirectory(tempFolder.toFile());
							} catch (final IOException e) {
								System.out.println("Could not clean up temp files for: " + MultiASR.class.getCanonicalName());
							}
						}
					});

					final Path ffmpegPath = Paths.get(props.getProperty(FFMPEG_PROP));
					if (!Files.isRegularFile(ffmpegPath) || !Files.isReadable(ffmpegPath) || !Files.isExecutable(ffmpegPath)) {
						throw new MultiASRError(ffmpegPath.toString() + " is not a valid path to ffmpeg");
					}
					this.ffmpeg = new FFmpeg(ffmpegPath.toString());
				} catch (final IOException e) {
					throw new MultiASRError(e);
				}
				final Path ffprobePath = Paths.get(props.getProperty(FFPROBE_PROP));
				if (!Files.isRegularFile(ffprobePath) || !Files.isReadable(ffprobePath) || !Files.isExecutable(ffprobePath)) {
					throw new MultiASRError(ffprobePath.toString() + " is not a valid path to ffprobe");
				}
				this.ffprobe = new FFprobe(ffprobePath.toString());
				this.ffmpegExecutor = new FFmpegExecutor(this.ffmpeg, this.ffprobe);
			}

			@Override
			public boolean register(IASR asr) {
				return this.asrs.add(asr);
			}

			@Override
			public List<ASROutput> recognize(Path sourceAudio) {
				return recognize(sourceAudio, new EmptyMap<>(), new EmptyMap<>());
			}

			private Path convertAudio(AudioFormat targetFormat, Path source) throws IOException {
				Path tempFile;

				if (!Strings.isNullOrEmpty(targetFormat.getFormat())) {
					tempFile = Files.createTempFile(tempFolder, null, "." + targetFormat.getFormat());
				} else {
					tempFile = Files.createTempFile(tempFolder, null, null);
				}

				final FFmpegBuilder ffmpegb = this.ffmpeg.buider();

				ffmpegb.setInput(ffprobe.probe(source.toString()));

				final FFmpegOutputBuilder outputb = ffmpegb.addOutput(tempFile.toString());
				if (!Strings.isNullOrEmpty(targetFormat.getCodec())) {
					outputb.setAudioCodec(targetFormat.getCodec());
				}
				if (targetFormat.getChannels() > 0) {
					outputb.setAudioChannels(targetFormat.getChannels());
				}
				if (targetFormat.getBitrate() > 0) {
					outputb.setAudioBitRate(targetFormat.getBitrate());
				}
				if (!Strings.isNullOrEmpty(targetFormat.getFormat())) {
					outputb.setFormat(targetFormat.getFormat());
				}
				if (targetFormat.getSamplingRate() > 0) {
					outputb.setAudioSampleRate(targetFormat.getSamplingRate());
				}
				outputb.done();

				final FFmpegJob job = this.ffmpegExecutor.createJob(ffmpegb);

				job.run();

				if (job.getState() == State.FAILED) {
					return null;
				}

				return tempFile;
			}

			@Override
			public List<ASROutput> recognize(Path sourceAudio, Map<String, String> requiredCapabilites, Map<String, String> optionalCapabilites) {
				try {
					sourceAudio = sourceAudio.toRealPath(LinkOption.NOFOLLOW_LINKS);
				} catch (final IOException e) {
					e.printStackTrace();
					return null;
				}
				final List<ASROutput> results = new ArrayList<>(this.asrs.size());

				if (requiredCapabilites == null) {
					requiredCapabilites = new EmptyMap<>();
				}
				if (optionalCapabilites == null) {
					optionalCapabilites = new EmptyMap<>();
				}

				FFmpegProbeResult probe;
				try {
					probe = ffprobe.probe(sourceAudio.toString());
				} catch (final IOException e) {
					e.printStackTrace();
					return null;
				}
				final AudioFormat sourceFormat = new AudioFormat() {
					@Override
					public int getChannels() {
						return probe.getStreams().size();
					}

					@Override
					public int getSamplingRate() {
						return probe.getStreams().get(0).time_base.getDenominator();
					}

					@Override
					public String getCodec() {
						return probe.getStreams().get(0).codec_name;
					}

					@Override
					public String getFormat() {
						return probe.getFormat().format_name;
					}

					@Override
					public int getBitrate() {
						return probe.getFormat().bit_rate;
					}

				};

				for (final IASR asr : this.asrs) {
					if (!asr.getSupportedCapabilities().containsAll(requiredCapabilites.keySet())) {
						continue;
					}
					Path converted = null;
					for (final AudioFormat format : asr.getSupportedAudioFormats()) {
						if (this.audioFormatMatcher.match(sourceFormat, format)) {
							try {
								//copy input files in case ASR does something to it
								final Path tempFile = Files.createTempFile(tempFolder, null, null);
								converted = Files.copy(sourceAudio, tempFile, StandardCopyOption.REPLACE_EXISTING);
							} catch (final IOException e) {
								e.printStackTrace();
							}

							break;
						}
						try {
							converted = this.convertAudio(format, sourceAudio);
						} catch (final IOException e) {
							e.printStackTrace();
						}
						if (converted != null) {
							break;
						}
					}

					if (converted != null) {
						final Map<String, String> capabilities = new TreeMap<>(requiredCapabilites);

						capabilities.putAll(optionalCapabilites);

						capabilities.keySet().retainAll(asr.getSupportedCapabilities());

						final List<ASROutput> result = asr.recognize(sourceAudio.toUri(), converted, capabilities);
						if (result != null) {
							results.addAll(result);
						}

						try {
							Files.delete(converted);
						} catch (final IOException e) {
							logger.warn("Could not delete temp file: " + converted.toString());
						}
					}
				}

				for (final IPostProcessor pp : this.pps) {
					final ListIterator<ASROutput> iter = results.listIterator();
					while (iter.hasNext()) {
						final ASROutput newResult = pp.process(iter.next());
						if (newResult != null) {
							iter.set(newResult);
						} else {
							iter.remove();
						}
					}
				}

				return results;
			}

			public List<IASR> autoRegisterASRs() {
				final ServiceLoader<IASR> sl = ServiceLoader.load(IASR.class);
				final List<IASR> asrs = new ArrayList<>();
				for (final IASR asr : sl) {
					if (this.register(asr)) {
						asrs.add(asr);
					}
				}
				return asrs;
			}

			public List<IPostProcessor> autoRegisterPostProcessors() {
				final ServiceLoader<IPostProcessor> sl = ServiceLoader.load(IPostProcessor.class);
				final List<IPostProcessor> pps = new ArrayList<>();
				for (final IPostProcessor pp : sl) {
					if (this.registerPostProcessor(pp)) {
						pps.add(pp);
					}
				}
				return pps;
			}

			@Override
			public boolean registerPostProcessor(IPostProcessor pp) {
				return this.pps.add(pp);
			}
}
