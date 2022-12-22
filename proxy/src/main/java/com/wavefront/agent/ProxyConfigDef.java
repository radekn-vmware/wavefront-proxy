package com.wavefront.agent;

import com.beust.jcommander.Parameter;
import com.wavefront.agent.auth.TokenValidationMethod;
import com.wavefront.agent.config.Configuration;
import com.wavefront.agent.data.TaskQueueLevel;

import static com.wavefront.agent.ProxyConfig.GRAPHITE_LISTENING_PORT;
import static com.wavefront.agent.data.EntityProperties.*;
import static com.wavefront.common.Utils.getLocalHostName;

/**
 * Proxy configuration (refactored from {@link AbstractAgent}).
 */

public abstract class ProxyConfigDef extends Configuration {
  @Parameter(
      names = {"--privateCertPath"},
      description =
          "TLS certificate path to use for securing all the ports. "
              + "X.509 certificate chain file in PEM format.")
  protected String privateCertPath = "";

  @Parameter(
      names = {"--privateKeyPath"},
      description =
          "TLS private key path to use for securing all the ports. "
              + "PKCS#8 private key file in PEM format.")
  protected String privateKeyPath = "";

  @Parameter(
      names = {"--tlsPorts"},
      description =
          "Comma-separated list of ports to be secured using TLS. "
              + "All ports will be secured when * specified.")
  protected String tlsPorts = "";

  @Parameter(
      names = {"--trafficShaping"},
      description =
          "Enables intelligent traffic shaping "
              + "based on received rate over last 5 minutes. Default: disabled",
      arity = 1)
  protected boolean trafficShaping = false;

  @Parameter(
      names = {"--trafficShapingWindowSeconds"},
      description =
          "Sets the width "
              + "(in seconds) for the sliding time window which would be used to calculate received "
              + "traffic rate. Default: 600 (10 minutes)")
  protected int trafficShapingWindowSeconds = 600;

  @Parameter(
      names = {"--trafficShapingHeadroom"},
      description =
          "Sets the headroom multiplier "
              + " to use for traffic shaping when there's backlog. Default: 1.15 (15% headroom)")
  protected double trafficShapingHeadroom = 1.15;

  @Parameter(
      names = {"--corsEnabledPorts"},
      description =
          "Enables CORS for specified "
              + "comma-delimited list of listening ports. Default: none (CORS disabled)")
  protected String corsEnabledPorts = "";

  @Parameter(
      names = {"--corsOrigin"},
      description =
          "Allowed origin for CORS requests, " + "or '*' to allow everything. Default: none")
  protected String corsOrigin = "";

  @Parameter(
      names = {"--corsAllowNullOrigin"},
      description = "Allow 'null' origin for CORS " + "requests. Default: false")
  protected boolean corsAllowNullOrigin = false;

  @Parameter(
      names = {"--help"},
      help = true)
  boolean help = false;

  @Parameter(
      names = {"--version"},
      description = "Print version and exit.",
      order = 0)
  boolean version = false;

  @Parameter(
      names = {"-f", "--file"},
      description = "Proxy configuration file",
      order = 2)
  String pushConfigFile = null;

  @Parameter(
      names = {"-p", "--prefix"},
      description = "Prefix to prepend to all push metrics before reporting.")
  String prefix = null;

  @Parameter(
      names = {"-t", "--token"},
      description = "Token to auto-register proxy with an account",
      order = 1)
  @ProxyConfigOption(category = "General", subCategory = "WF Server", hide = true)
  String token = "undefined";

  @Parameter(
      names = {"--testLogs"},
      description = "Run interactive session for crafting logsIngestionConfig.yaml")
  boolean testLogs = false;

  @Parameter(
      names = {"--testPreprocessorForPort"},
      description =
          "Run interactive session for " + "testing preprocessor rules for specified port")
  String testPreprocessorForPort = null;

  @Parameter(
      names = {"--testSpanPreprocessorForPort"},
      description =
          "Run interactive session " + "for testing preprocessor span rules for specifierd port")
  String testSpanPreprocessorForPort = null;

  @Parameter(
      names = {"--server", "-h", "--host"},
      description = "Server URL",
      order = 0)
  @ProxyConfigOption(category = "General", subCategory = "WF Server")
  String server = "http://localhost:8080/api/";

  @Parameter(
      names = {"--buffer"},
      description =
          "File name prefix to use for buffering "
              + "transmissions to be retried. Defaults to /var/spool/wavefront-proxy/buffer.",
      order = 4)
  @ProxyConfigOption(category = "Buffer", subCategory = "Disk")
  String bufferFile = "/var/spool/wavefront-proxy/buffer";

  @Parameter(
      names = {"--bufferShardSize"},
      description =
          "Buffer file partition size, in MB. "
              + "Setting this value too low may reduce the efficiency of disk space utilization, "
              + "while setting this value too high will allocate disk space in larger increments. "
              + "Default: 128")
  int bufferShardSize = 128;

  @Parameter(
      names = {"--disableBufferSharding"},
      description = "Use single-file buffer " + "(legacy functionality). Default: false",
      arity = 1)
  boolean disableBufferSharding = false;

  @Parameter(
      names = {"--sqsBuffer"},
      description =
          "Use AWS SQS Based for buffering transmissions " + "to be retried. Defaults to False",
      arity = 1)
  boolean sqsQueueBuffer = false;

  @Parameter(
      names = {"--sqsQueueNameTemplate"},
      description =
          "The replacement pattern to use for naming the "
              + "sqs queues. e.g. wf-proxy-{{id}}-{{entity}}-{{port}} would result in a queue named wf-proxy-id-points-2878")
  @ProxyConfigOption(category = "Buffer", subCategory = "External")
  String sqsQueueNameTemplate = "wf-proxy-{{id}}-{{entity}}-{{port}}";

  @Parameter(
      names = {"--sqsQueueIdentifier"},
      description = "An identifier for identifying these proxies in SQS")
  @ProxyConfigOption(category = "Buffer", subCategory = "External")
  String sqsQueueIdentifier = null;

  @Parameter(
      names = {"--sqsQueueRegion"},
      description = "The AWS Region name the queue will live in.")
  @ProxyConfigOption(category = "Buffer", subCategory = "External")
  String sqsQueueRegion = "us-west-2";

  @Parameter(
      names = {"--taskQueueLevel"},
      converter = ProxyConfig.TaskQueueLevelConverter.class,
      description =
          "Sets queueing strategy. Allowed values: MEMORY, PUSHBACK, ANY_ERROR. "
              + "Default: ANY_ERROR")
  TaskQueueLevel taskQueueLevel = TaskQueueLevel.ANY_ERROR;

  @Parameter(
      names = {"--exportQueuePorts"},
      description =
          "Export queued data in plaintext "
              + "format for specified ports (comma-delimited list) and exit. Set to 'all' to export "
              + "everything. Default: none")
  String exportQueuePorts = null;

  @Parameter(
      names = {"--exportQueueOutputFile"},
      description =
          "Export queued data in plaintext "
              + "format for specified ports (comma-delimited list) and exit. Default: none")
  String exportQueueOutputFile = null;

  @Parameter(
      names = {"--exportQueueRetainData"},
      description = "Whether to retain data in the " + "queue during export. Defaults to true.",
      arity = 1)
  boolean exportQueueRetainData = true;

  @Parameter(
      names = {"--useNoopSender"},
      description =
          "Run proxy in debug/performance test "
              + "mode and discard all received data. Default: false",
      arity = 1)
  boolean useNoopSender = false;

  @Parameter(
      names = {"--flushThreads"},
      description =
          "Number of threads that flush data to the server. Defaults to"
              + "the number of processors (min. 4). Setting this value too large will result in sending batches that are too "
              + "small to the server and wasting connections. This setting is per listening port.")
  int flushThreads = Math.min(16, Math.max(4, Runtime.getRuntime().availableProcessors()));

  @Parameter(
      names = {"--flushThreadsSourceTags"},
      description = "Number of threads that send " + "source tags data to the server. Default: 2")
  int flushThreadsSourceTags = DEFAULT_FLUSH_THREADS_SOURCE_TAGS;

  @Parameter(
      names = {"--flushThreadsEvents"},
      description = "Number of threads that send " + "event data to the server. Default: 2")
  int flushThreadsEvents = DEFAULT_FLUSH_THREADS_EVENTS;

  @Parameter(
      names = {"--flushThreadsLogs"},
      description =
          "Number of threads that flush data to "
              + "the server. Defaults to the number of processors (min. 4). Setting this value too large "
              + "will result in sending batches that are too small to the server and wasting connections. This setting is per listening port.",
      order = 5)
  int flushThreadsLogs = Math.min(16, Math.max(4, Runtime.getRuntime().availableProcessors()));

  @Parameter(
      names = {"--purgeBuffer"},
      description = "Whether to purge the retry buffer on start-up. Defaults to " + "false.",
      arity = 1)
  boolean purgeBuffer = false;

  @Parameter(
      names = {"--pushFlushInterval"},
      description = "Milliseconds between batches. " + "Defaults to 1000 ms")
  @ProxyConfigOption(category = "Output", subCategory = "push")
  int pushFlushInterval = DEFAULT_FLUSH_INTERVAL;

  @Parameter(
      names = {"--pushFlushIntervalLogs"},
      description = "Milliseconds between batches. Defaults to 1000 ms")
  @ProxyConfigOption(category = "Output", subCategory = "Logs")
  int pushFlushIntervalLogs = DEFAULT_FLUSH_INTERVAL;

  @Parameter(
      names = {"--pushFlushMaxPoints"},
      description = "Maximum allowed points " + "in a single flush. Defaults: 40000")
  @ProxyConfigOption(category = "Output", subCategory = "Max")
  int pushFlushMaxPoints = DEFAULT_BATCH_SIZE;

  @Parameter(
      names = {"--pushFlushMaxHistograms"},
      description = "Maximum allowed histograms " + "in a single flush. Default: 10000")
  @ProxyConfigOption(category = "Output", subCategory = "Max")
  int pushFlushMaxHistograms = DEFAULT_BATCH_SIZE_HISTOGRAMS;

  @Parameter(
      names = {"--pushFlushMaxSourceTags"},
      description = "Maximum allowed source tags " + "in a single flush. Default: 50")
  @ProxyConfigOption(category = "Output", subCategory = "Max")
  int pushFlushMaxSourceTags = DEFAULT_BATCH_SIZE_SOURCE_TAGS;

  @Parameter(
      names = {"--pushFlushMaxSpans"},
      description = "Maximum allowed spans " + "in a single flush. Default: 5000")
  int pushFlushMaxSpans = DEFAULT_BATCH_SIZE_SPANS;

  @Parameter(
      names = {"--pushFlushMaxSpanLogs"},
      description = "Maximum allowed span logs " + "in a single flush. Default: 1000")
  int pushFlushMaxSpanLogs = DEFAULT_BATCH_SIZE_SPAN_LOGS;

  @Parameter(
      names = {"--pushFlushMaxEvents"},
      description = "Maximum allowed events " + "in a single flush. Default: 50")
  int pushFlushMaxEvents = DEFAULT_BATCH_SIZE_EVENTS;

  @Parameter(
      names = {"--pushFlushMaxLogs"},
      description =
          "Maximum size of a log payload "
              + "in a single flush in bytes between 1mb (1048576) and 5mb (5242880). Default: 4mb (4194304)")
  int pushFlushMaxLogs = DEFAULT_BATCH_SIZE_LOGS_PAYLOAD;

  @Parameter(
      names = {"--pushRateLimit"},
      description = "Limit the outgoing point rate at the proxy. Default: " + "do not throttle.")
  double pushRateLimit = NO_RATE_LIMIT;

  @Parameter(
      names = {"--pushRateLimitHistograms"},
      description =
          "Limit the outgoing histogram " + "rate at the proxy. Default: do not throttle.")
  double pushRateLimitHistograms = NO_RATE_LIMIT;

  @Parameter(
      names = {"--pushRateLimitSourceTags"},
      description = "Limit the outgoing rate " + "for source tags at the proxy. Default: 5 op/s")
  double pushRateLimitSourceTags = 5.0d;

  @Parameter(
      names = {"--pushRateLimitSpans"},
      description =
          "Limit the outgoing tracing spans " + "rate at the proxy. Default: do not throttle.")
  double pushRateLimitSpans = NO_RATE_LIMIT;

  @Parameter(
      names = {"--pushRateLimitSpanLogs"},
      description =
          "Limit the outgoing span logs " + "rate at the proxy. Default: do not throttle.")
  double pushRateLimitSpanLogs = NO_RATE_LIMIT;

  @Parameter(
      names = {"--pushRateLimitEvents"},
      description = "Limit the outgoing rate " + "for events at the proxy. Default: 5 events/s")
  double pushRateLimitEvents = 5.0d;

  @Parameter(
      names = {"--pushRateLimitLogs"},
      description =
          "Limit the outgoing logs " + "data rate at the proxy. Default: do not throttle.")
  double pushRateLimitLogs = NO_RATE_LIMIT_BYTES;

  @Parameter(
      names = {"--pushRateLimitMaxBurstSeconds"},
      description =
          "Max number of burst seconds to allow "
              + "when rate limiting to smooth out uneven traffic. Set to 1 when doing data backfills. Default: 10")
  int pushRateLimitMaxBurstSeconds = 10;

  @Parameter(
      names = {"--pushMemoryBufferLimit"},
      description =
          "Max number of points that can stay in memory buffers"
              + " before spooling to disk. Defaults to 16 * pushFlushMaxPoints, minimum size: pushFlushMaxPoints. Setting this "
              + " value lower than default reduces memory usage but will force the proxy to spool to disk more frequently if "
              + " you have points arriving at the proxy in short bursts")
  int pushMemoryBufferLimit = 16 * pushFlushMaxPoints;

  @Parameter(
      names = {"--pushMemoryBufferLimitLogs"},
      description =
          "Max number of logs that "
              + "can stay in memory buffers before spooling to disk. Defaults to 16 * pushFlushMaxLogs, "
              + "minimum size: pushFlushMaxLogs. Setting this value lower than default reduces memory usage "
              + "but will force the proxy to spool to disk more frequently if you have points arriving at the "
              + "proxy in short bursts")
  int pushMemoryBufferLimitLogs = 16 * pushFlushMaxLogs;

  @Parameter(
      names = {"--pushBlockedSamples"},
      description = "Max number of blocked samples to print to log. Defaults" + " to 5.")
  int pushBlockedSamples = 5;

  @Parameter(
      names = {"--blockedPointsLoggerName"},
      description = "Logger Name for blocked " + "points. " + "Default: RawBlockedPoints")
  String blockedPointsLoggerName = "RawBlockedPoints";

  @Parameter(
      names = {"--blockedHistogramsLoggerName"},
      description = "Logger Name for blocked " + "histograms" + "Default: RawBlockedPoints")
  String blockedHistogramsLoggerName = "RawBlockedPoints";

  @Parameter(
      names = {"--blockedSpansLoggerName"},
      description = "Logger Name for blocked spans" + "Default: RawBlockedPoints")
  String blockedSpansLoggerName = "RawBlockedPoints";

  @Parameter(
      names = {"--blockedLogsLoggerName"},
      description = "Logger Name for blocked logs" + "Default: RawBlockedLogs")
  String blockedLogsLoggerName = "RawBlockedLogs";

  @Parameter(
      names = {"--pushListenerPorts"},
      description = "Comma-separated list of ports to listen on. Defaults to " + "2878.")
  String pushListenerPorts = "" + GRAPHITE_LISTENING_PORT;

  @Parameter(
      names = {"--pushListenerMaxReceivedLength"},
      description =
          "Maximum line length for received points in"
              + " plaintext format on Wavefront/OpenTSDB/Graphite ports. Default: 32768 (32KB)")
  int pushListenerMaxReceivedLength = 32768;

  @Parameter(
      names = {"--pushListenerHttpBufferSize"},
      description =
          "Maximum allowed request size (in bytes) for"
              + " incoming HTTP requests on Wavefront/OpenTSDB/Graphite ports (Default: 16MB)")
  int pushListenerHttpBufferSize = 16 * 1024 * 1024;

  @Parameter(
      names = {"--traceListenerMaxReceivedLength"},
      description = "Maximum line length for received spans and" + " span logs (Default: 1MB)")
  int traceListenerMaxReceivedLength = 1024 * 1024;

  @Parameter(
      names = {"--traceListenerHttpBufferSize"},
      description =
          "Maximum allowed request size (in bytes) for"
              + " incoming HTTP requests on tracing ports (Default: 16MB)")
  int traceListenerHttpBufferSize = 16 * 1024 * 1024;

  @Parameter(
      names = {"--listenerIdleConnectionTimeout"},
      description =
          "Close idle inbound connections after " + " specified time in seconds. Default: 300")
  int listenerIdleConnectionTimeout = 300;

  @Parameter(
      names = {"--memGuardFlushThreshold"},
      description =
          "If heap usage exceeds this threshold (in percent), "
              + "flush pending points to disk as an additional OoM protection measure. Set to 0 to disable. Default: 99")
  int memGuardFlushThreshold = 98;

  @Parameter(
      names = {"--histogramPassthroughRecompression"},
      description =
          "Whether we should recompress histograms received on pushListenerPorts. "
              + "Default: true",
      arity = 1)
  boolean histogramPassthroughRecompression = true;

  @Parameter(
      names = {"--histogramStateDirectory"},
      description = "Directory for persistent proxy state, must be writable.")
  String histogramStateDirectory = "/var/spool/wavefront-proxy";

  @Parameter(
      names = {"--histogramAccumulatorResolveInterval"},
      description =
          "Interval to write-back accumulation changes from memory cache to disk in "
              + "millis (only applicable when memory cache is enabled")
  long histogramAccumulatorResolveInterval = 5000L;

  @Parameter(
      names = {"--histogramAccumulatorFlushInterval"},
      description =
          "Interval to check for histograms to send to Wavefront in millis. " + "(Default: 10000)")
  long histogramAccumulatorFlushInterval = 10000L;

  @Parameter(
      names = {"--histogramAccumulatorFlushMaxBatchSize"},
      description =
          "Max number of histograms to send to Wavefront in one flush " + "(Default: no limit)")
  int histogramAccumulatorFlushMaxBatchSize = -1;

  @Parameter(
      names = {"--histogramMaxReceivedLength"},
      description = "Maximum line length for received histogram data (Default: 65536)")
  int histogramMaxReceivedLength = 64 * 1024;

  @Parameter(
      names = {"--histogramHttpBufferSize"},
      description =
          "Maximum allowed request size (in bytes) for incoming HTTP requests on "
              + "histogram ports (Default: 16MB)")
  int histogramHttpBufferSize = 16 * 1024 * 1024;

  @Parameter(
      names = {"--histogramMinuteListenerPorts"},
      description = "Comma-separated list of ports to listen on. Defaults to none.")
  String histogramMinuteListenerPorts = "";

  @Parameter(
      names = {"--histogramMinuteFlushSecs"},
      description =
          "Number of seconds to keep a minute granularity accumulator open for " + "new samples.")
  int histogramMinuteFlushSecs = 70;

  @Parameter(
      names = {"--histogramMinuteCompression"},
      description = "Controls allowable number of centroids per histogram. Must be in [20;1000]")
  short histogramMinuteCompression = 32;

  @Parameter(
      names = {"--histogramMinuteAvgKeyBytes"},
      description =
          "Average number of bytes in a [UTF-8] encoded histogram key. Generally "
              + "corresponds to a metric, source and tags concatenation.")
  int histogramMinuteAvgKeyBytes = 150;

  @Parameter(
      names = {"--histogramMinuteAvgDigestBytes"},
      description = "Average number of bytes in a encoded histogram.")
  int histogramMinuteAvgDigestBytes = 500;

  @Parameter(
      names = {"--histogramMinuteAccumulatorSize"},
      description =
          "Expected upper bound of concurrent accumulations, ~ #timeseries * #parallel "
              + "reporting bins")
  long histogramMinuteAccumulatorSize = 100000L;

  @Parameter(
      names = {"--histogramMinuteAccumulatorPersisted"},
      arity = 1,
      description = "Whether the accumulator should persist to disk")
  boolean histogramMinuteAccumulatorPersisted = false;

  @Parameter(
      names = {"--histogramMinuteMemoryCache"},
      arity = 1,
      description =
          "Enabling memory cache reduces I/O load with fewer time series and higher "
              + "frequency data (more than 1 point per second per time series). Default: false")
  boolean histogramMinuteMemoryCache = false;

  @Parameter(
      names = {"--histogramHourListenerPorts"},
      description = "Comma-separated list of ports to listen on. Defaults to none.")
  String histogramHourListenerPorts = "";

  @Parameter(
      names = {"--histogramHourFlushSecs"},
      description =
          "Number of seconds to keep an hour granularity accumulator open for " + "new samples.")
  int histogramHourFlushSecs = 4200;

  @Parameter(
      names = {"--histogramHourCompression"},
      description = "Controls allowable number of centroids per histogram. Must be in [20;1000]")
  short histogramHourCompression = 32;

  @Parameter(
      names = {"--histogramHourAvgKeyBytes"},
      description =
          "Average number of bytes in a [UTF-8] encoded histogram key. Generally "
              + " corresponds to a metric, source and tags concatenation.")
  int histogramHourAvgKeyBytes = 150;

  @Parameter(
      names = {"--histogramHourAvgDigestBytes"},
      description = "Average number of bytes in a encoded histogram.")
  int histogramHourAvgDigestBytes = 500;

  @Parameter(
      names = {"--histogramHourAccumulatorSize"},
      description =
          "Expected upper bound of concurrent accumulations, ~ #timeseries * #parallel "
              + "reporting bins")
  long histogramHourAccumulatorSize = 100000L;

  @Parameter(
      names = {"--histogramHourAccumulatorPersisted"},
      arity = 1,
      description = "Whether the accumulator should persist to disk")
  boolean histogramHourAccumulatorPersisted = false;

  @Parameter(
      names = {"--histogramHourMemoryCache"},
      arity = 1,
      description =
          "Enabling memory cache reduces I/O load with fewer time series and higher "
              + "frequency data (more than 1 point per second per time series). Default: false")
  boolean histogramHourMemoryCache = false;

  @Parameter(
      names = {"--histogramDayListenerPorts"},
      description = "Comma-separated list of ports to listen on. Defaults to none.")
  String histogramDayListenerPorts = "";

  @Parameter(
      names = {"--histogramDayFlushSecs"},
      description = "Number of seconds to keep a day granularity accumulator open for new samples.")
  int histogramDayFlushSecs = 18000;

  @Parameter(
      names = {"--histogramDayCompression"},
      description = "Controls allowable number of centroids per histogram. Must be in [20;1000]")
  short histogramDayCompression = 32;

  @Parameter(
      names = {"--histogramDayAvgKeyBytes"},
      description =
          "Average number of bytes in a [UTF-8] encoded histogram key. Generally "
              + "corresponds to a metric, source and tags concatenation.")
  int histogramDayAvgKeyBytes = 150;

  @Parameter(
      names = {"--histogramDayAvgHistogramDigestBytes"},
      description = "Average number of bytes in a encoded histogram.")
  int histogramDayAvgDigestBytes = 500;

  @Parameter(
      names = {"--histogramDayAccumulatorSize"},
      description =
          "Expected upper bound of concurrent accumulations, ~ #timeseries * #parallel "
              + "reporting bins")
  long histogramDayAccumulatorSize = 100000L;

  @Parameter(
      names = {"--histogramDayAccumulatorPersisted"},
      arity = 1,
      description = "Whether the accumulator should persist to disk")
  boolean histogramDayAccumulatorPersisted = false;

  @Parameter(
      names = {"--histogramDayMemoryCache"},
      arity = 1,
      description =
          "Enabling memory cache reduces I/O load with fewer time series and higher "
              + "frequency data (more than 1 point per second per time series). Default: false")
  boolean histogramDayMemoryCache = false;

  @Parameter(
      names = {"--histogramDistListenerPorts"},
      description = "Comma-separated list of ports to listen on. Defaults to none.")
  String histogramDistListenerPorts = "";

  @Parameter(
      names = {"--histogramDistFlushSecs"},
      description = "Number of seconds to keep a new distribution bin open for new samples.")
  int histogramDistFlushSecs = 70;

  @Parameter(
      names = {"--histogramDistCompression"},
      description = "Controls allowable number of centroids per histogram. Must be in [20;1000]")
  short histogramDistCompression = 32;

  @Parameter(
      names = {"--histogramDistAvgKeyBytes"},
      description =
          "Average number of bytes in a [UTF-8] encoded histogram key. Generally "
              + "corresponds to a metric, source and tags concatenation.")
  int histogramDistAvgKeyBytes = 150;

  @Parameter(
      names = {"--histogramDistAvgDigestBytes"},
      description = "Average number of bytes in a encoded histogram.")
  int histogramDistAvgDigestBytes = 500;

  @Parameter(
      names = {"--histogramDistAccumulatorSize"},
      description =
          "Expected upper bound of concurrent accumulations, ~ #timeseries * #parallel "
              + "reporting bins")
  long histogramDistAccumulatorSize = 100000L;

  @Parameter(
      names = {"--histogramDistAccumulatorPersisted"},
      arity = 1,
      description = "Whether the accumulator should persist to disk")
  boolean histogramDistAccumulatorPersisted = false;

  @Parameter(
      names = {"--histogramDistMemoryCache"},
      arity = 1,
      description =
          "Enabling memory cache reduces I/O load with fewer time series and higher "
              + "frequency data (more than 1 point per second per time series). Default: false")
  boolean histogramDistMemoryCache = false;

  @Parameter(
      names = {"--graphitePorts"},
      description =
          "Comma-separated list of ports to listen on for graphite "
              + "data. Defaults to empty list.")
  String graphitePorts = "";

  @Parameter(
      names = {"--graphiteFormat"},
      description =
          "Comma-separated list of metric segments to extract and "
              + "reassemble as the hostname (1-based).")
  String graphiteFormat = "";

  @Parameter(
      names = {"--graphiteDelimiters"},
      description =
          "Concatenated delimiters that should be replaced in the "
              + "extracted hostname with dots. Defaults to underscores (_).")
  String graphiteDelimiters = "_";

  @Parameter(
      names = {"--graphiteFieldsToRemove"},
      description = "Comma-separated list of metric segments to remove (1-based)")
  String graphiteFieldsToRemove;

  @Parameter(
      names = {"--jsonListenerPorts", "--httpJsonPorts"},
      description =
          "Comma-separated list of ports to "
              + "listen on for json metrics data. Binds, by default, to none.")
  String jsonListenerPorts = "";

  @Parameter(
      names = {"--dataDogJsonPorts"},
      description =
          "Comma-separated list of ports to listen on for JSON "
              + "metrics data in DataDog format. Binds, by default, to none.")
  String dataDogJsonPorts = "";

  @Parameter(
      names = {"--dataDogRequestRelayTarget"},
      description =
          "HTTP/HTTPS target for relaying all incoming "
              + "requests on dataDogJsonPorts to. Defaults to none (do not relay incoming requests)")
  String dataDogRequestRelayTarget = null;

  @Parameter(
      names = {"--dataDogRequestRelayAsyncThreads"},
      description =
          "Max number of "
              + "in-flight HTTP requests being relayed to dataDogRequestRelayTarget. Default: 32")
  int dataDogRequestRelayAsyncThreads = 32;

  @Parameter(
      names = {"--dataDogRequestRelaySyncMode"},
      description =
          "Whether we should wait "
              + "until request is relayed successfully before processing metrics. Default: false")
  boolean dataDogRequestRelaySyncMode = false;

  @Parameter(
      names = {"--dataDogProcessSystemMetrics"},
      description =
          "If true, handle system metrics as reported by "
              + "DataDog collection agent. Defaults to false.",
      arity = 1)
  boolean dataDogProcessSystemMetrics = false;

  @Parameter(
      names = {"--dataDogProcessServiceChecks"},
      description = "If true, convert service checks to metrics. " + "Defaults to true.",
      arity = 1)
  boolean dataDogProcessServiceChecks = true;

  @Parameter(
      names = {"--writeHttpJsonListenerPorts", "--writeHttpJsonPorts"},
      description =
          "Comma-separated list "
              + "of ports to listen on for json metrics from collectd write_http json format data. Binds, by default, to none.")
  String writeHttpJsonListenerPorts = "";

  @Parameter(
      names = {"--otlpGrpcListenerPorts"},
      description =
          "Comma-separated list of ports to"
              + " listen on for OpenTelemetry/OTLP Protobuf formatted data over gRPC. Binds, by default, to"
              + " none (4317 is recommended).")
  String otlpGrpcListenerPorts = "";

  @Parameter(
      names = {"--otlpHttpListenerPorts"},
      description =
          "Comma-separated list of ports to"
              + " listen on for OpenTelemetry/OTLP Protobuf formatted data over HTTP. Binds, by default, to"
              + " none (4318 is recommended).")
  String otlpHttpListenerPorts = "";

  @Parameter(
      names = {"--otlpResourceAttrsOnMetricsIncluded"},
      arity = 1,
      description = "If true, includes OTLP resource attributes on metrics (Default: false)")
  boolean otlpResourceAttrsOnMetricsIncluded = false;

  @Parameter(
      names = {"--otlpAppTagsOnMetricsIncluded"},
      arity = 1,
      description =
          "If true, includes the following application-related resource attributes on "
              + "metrics: application, service.name, shard, cluster (Default: true)")
  boolean otlpAppTagsOnMetricsIncluded = true;
  // logs ingestion
  @Parameter(
      names = {"--filebeatPort"},
      description = "Port on which to listen for filebeat data.")
  int filebeatPort = 0;

  @Parameter(
      names = {"--rawLogsPort"},
      description = "Port on which to listen for raw logs data.")
  int rawLogsPort = 0;

  @Parameter(
      names = {"--rawLogsMaxReceivedLength"},
      description = "Maximum line length for received raw logs (Default: 4096)")
  int rawLogsMaxReceivedLength = 4096;

  @Parameter(
      names = {"--rawLogsHttpBufferSize"},
      description =
          "Maximum allowed request size (in bytes) for"
              + " incoming HTTP requests with raw logs (Default: 16MB)")
  int rawLogsHttpBufferSize = 16 * 1024 * 1024;

  @Parameter(
      names = {"--logsIngestionConfigFile"},
      description = "Location of logs ingestions config yaml file.")
  String logsIngestionConfigFile = "/etc/wavefront/wavefront-proxy/logsingestion.yaml";
  /**
   * Deprecated property, please use proxyname config field to set proxy name. Default hostname to
   * FQDN of machine. Sent as internal metric tag with checkin.
   */
  @Parameter(
      names = {"--hostname"},
      description = "Hostname for the proxy. Defaults to FQDN of machine.")
  String hostname = getLocalHostName();
  /** This property holds the proxy name. Default proxyname to FQDN of machine. */
  @Parameter(
      names = {"--proxyname"},
      description = "Name for the proxy. Defaults to hostname.")
  String proxyname = getLocalHostName();

  @Parameter(
      names = {"--idFile"},
      description =
          "File to read proxy id from. Defaults to ~/.dshell/id."
              + "This property is ignored if ephemeral=true.")
  String idFile = null;

  @Parameter(
      names = {"--allowRegex", "--whitelistRegex"},
      description =
          "Regex pattern (java"
              + ".util.regex) that graphite input lines must match to be accepted")
  String allowRegex;

  @Parameter(
      names = {"--blockRegex", "--blacklistRegex"},
      description =
          "Regex pattern (java"
              + ".util.regex) that graphite input lines must NOT match to be accepted")
  String blockRegex;

  @Parameter(
      names = {"--opentsdbPorts"},
      description =
          "Comma-separated list of ports to listen on for opentsdb data. "
              + "Binds, by default, to none.")
  String opentsdbPorts = "";

  @Parameter(
      names = {"--opentsdbAllowRegex", "--opentsdbWhitelistRegex"},
      description =
          "Regex "
              + "pattern (java.util.regex) that opentsdb input lines must match to be accepted")
  String opentsdbAllowRegex;

  @Parameter(
      names = {"--opentsdbBlockRegex", "--opentsdbBlacklistRegex"},
      description =
          "Regex "
              + "pattern (java.util.regex) that opentsdb input lines must NOT match to be accepted")
  String opentsdbBlockRegex;

  @Parameter(
      names = {"--picklePorts"},
      description =
          "Comma-separated list of ports to listen on for pickle protocol "
              + "data. Defaults to none.")
  String picklePorts;

  @Parameter(
      names = {"--traceListenerPorts"},
      description =
          "Comma-separated list of ports to listen on for trace " + "data. Defaults to none.")
  String traceListenerPorts;

  @Parameter(
      names = {"--traceJaegerListenerPorts"},
      description =
          "Comma-separated list of ports on which to listen "
              + "on for jaeger thrift formatted data over TChannel protocol. Defaults to none.")
  String traceJaegerListenerPorts;

  @Parameter(
      names = {"--traceJaegerHttpListenerPorts"},
      description =
          "Comma-separated list of ports on which to listen "
              + "on for jaeger thrift formatted data over HTTP. Defaults to none.")
  String traceJaegerHttpListenerPorts;

  @Parameter(
      names = {"--traceJaegerGrpcListenerPorts"},
      description =
          "Comma-separated list of ports on which to listen "
              + "on for jaeger Protobuf formatted data over gRPC. Defaults to none.")
  String traceJaegerGrpcListenerPorts;

  @Parameter(
      names = {"--traceJaegerApplicationName"},
      description = "Application name for Jaeger. Defaults to Jaeger.")
  String traceJaegerApplicationName;

  @Parameter(
      names = {"--traceZipkinListenerPorts"},
      description =
          "Comma-separated list of ports on which to listen "
              + "on for zipkin trace data over HTTP. Defaults to none.")
  String traceZipkinListenerPorts;

  @Parameter(
      names = {"--traceZipkinApplicationName"},
      description = "Application name for Zipkin. Defaults to Zipkin.")
  String traceZipkinApplicationName;

  @Parameter(
      names = {"--customTracingListenerPorts"},
      description =
          "Comma-separated list of ports to listen on spans from level 1 SDK. Helps "
              + "derive RED metrics and for the span and heartbeat for corresponding application at "
              + "proxy. Defaults: none")
  String customTracingListenerPorts = "";

  @Parameter(
      names = {"--customTracingApplicationName"},
      description =
          "Application name to use "
              + "for spans sent to customTracingListenerPorts when span doesn't have application tag. "
              + "Defaults to defaultApp.")
  String customTracingApplicationName;

  @Parameter(
      names = {"--customTracingServiceName"},
      description =
          "Service name to use for spans"
              + " sent to customTracingListenerPorts when span doesn't have service tag. "
              + "Defaults to defaultService.")
  String customTracingServiceName;

  @Parameter(
      names = {"--traceSamplingRate"},
      description = "Value between 0.0 and 1.0. " + "Defaults to 1.0 (allow all spans).")
  double traceSamplingRate = 1.0d;

  @Parameter(
      names = {"--traceSamplingDuration"},
      description =
          "Sample spans by duration in "
              + "milliseconds. "
              + "Defaults to 0 (ignore duration based sampling).")
  int traceSamplingDuration = 0;

  @Parameter(
      names = {"--traceDerivedCustomTagKeys"},
      description = "Comma-separated " + "list of custom tag keys for trace derived RED metrics.")
  String traceDerivedCustomTagKeys;

  @Parameter(
      names = {"--backendSpanHeadSamplingPercentIgnored"},
      description = "Ignore " + "spanHeadSamplingPercent config in backend CustomerSettings")
  boolean backendSpanHeadSamplingPercentIgnored = false;

  @Parameter(
      names = {"--pushRelayListenerPorts"},
      description =
          "Comma-separated list of ports on which to listen "
              + "on for proxy chaining data. For internal use. Defaults to none.")
  String pushRelayListenerPorts;

  @Parameter(
      names = {"--pushRelayHistogramAggregator"},
      description =
          "If true, aggregate "
              + "histogram distributions received on the relay port. Default: false",
      arity = 1)
  boolean pushRelayHistogramAggregator = false;

  @Parameter(
      names = {"--pushRelayHistogramAggregatorAccumulatorSize"},
      description =
          "Expected upper bound of concurrent accumulations, ~ #timeseries * #parallel "
              + "reporting bins")
  long pushRelayHistogramAggregatorAccumulatorSize = 100000L;

  @Parameter(
      names = {"--pushRelayHistogramAggregatorFlushSecs"},
      description = "Number of seconds to keep accumulator open for new samples.")
  int pushRelayHistogramAggregatorFlushSecs = 70;

  @Parameter(
      names = {"--pushRelayHistogramAggregatorCompression"},
      description =
          "Controls allowable number of centroids per histogram. Must be in [20;1000] "
              + "range. Default: 32")
  short pushRelayHistogramAggregatorCompression = 32;

  @Parameter(
      names = {"--splitPushWhenRateLimited"},
      description =
          "Whether to split the push "
              + "batch size when the push is rejected by Wavefront due to rate limit.  Default false.",
      arity = 1)
  boolean splitPushWhenRateLimited = DEFAULT_SPLIT_PUSH_WHEN_RATE_LIMITED;

  @Parameter(
      names = {"--retryBackoffBaseSeconds"},
      description =
          "For exponential backoff "
              + "when retry threads are throttled, the base (a in a^b) in seconds.  Default 2.0")
  double retryBackoffBaseSeconds = DEFAULT_RETRY_BACKOFF_BASE_SECONDS;

  @Parameter(
      names = {"--customSourceTags"},
      description =
          "Comma separated list of point tag "
              + "keys that should be treated as the source in Wavefront in the absence of a tag named "
              + "`source` or `host`. Default: fqdn")
  String customSourceTags = "fqdn";

  @Parameter(
      names = {"--agentMetricsPointTags"},
      description =
          "Additional point tags and their "
              + " respective values to be included into internal agent's metrics "
              + "(comma-separated list, ex: dc=west,env=prod). Default: none")
  String agentMetricsPointTags = null;

  @Parameter(
      names = {"--ephemeral"},
      arity = 1,
      description =
          "If true, this proxy is removed "
              + "from Wavefront after 24 hours of inactivity. Default: true")
  boolean ephemeral = true;

  @Parameter(
      names = {"--disableRdnsLookup"},
      arity = 1,
      description =
          "When receiving"
              + " Wavefront-formatted data without source/host specified, use remote IP address as source "
              + "instead of trying to resolve the DNS name. Default false.")
  boolean disableRdnsLookup = false;

  @Parameter(
      names = {"--gzipCompression"},
      arity = 1,
      description =
          "If true, enables gzip " + "compression for traffic sent to Wavefront (Default: true)")
  boolean gzipCompression = true;

  @Parameter(
      names = {"--gzipCompressionLevel"},
      description =
          "If gzipCompression is enabled, "
              + "sets compression level (1-9). Higher compression levels use more CPU. Default: 4")
  int gzipCompressionLevel = 4;

  @Parameter(
      names = {"--soLingerTime"},
      description =
          "If provided, enables SO_LINGER with the specified linger time in seconds (default: SO_LINGER disabled)")
  int soLingerTime = -1;

  @Parameter(
      names = {"--proxyHost"},
      description = "Proxy host for routing traffic through a http proxy")
  String proxyHost = null;

  @Parameter(
      names = {"--proxyPort"},
      description = "Proxy port for routing traffic through a http proxy")
  int proxyPort = 0;

  @Parameter(
      names = {"--proxyUser"},
      description =
          "If proxy authentication is necessary, this is the username that will be passed along")
  @ProxyConfigOption(category = "General", subCategory = "Http Proxy")
  String proxyUser = null;

  @Parameter(
      names = {"--proxyPassword"},
      description =
          "If proxy authentication is necessary, this is the password that will be passed along")
  @ProxyConfigOption(category = "General", subCategory = "Http Proxy", hide = true)
  String proxyPassword = null;

  @Parameter(
      names = {"--httpUserAgent"},
      description = "Override User-Agent in request headers")
  String httpUserAgent = null;

  @Parameter(
      names = {"--httpConnectTimeout"},
      description = "Connect timeout in milliseconds (default: 5000)")
  int httpConnectTimeout = 5000;

  @Parameter(
      names = {"--httpRequestTimeout"},
      description = "Request timeout in milliseconds (default: 10000)")
  int httpRequestTimeout = 10000;

  @Parameter(
      names = {"--httpMaxConnTotal"},
      description = "Max connections to keep open (default: 200)")
  int httpMaxConnTotal = 200;

  @Parameter(
      names = {"--httpMaxConnPerRoute"},
      description = "Max connections per route to keep open (default: 100)")
  int httpMaxConnPerRoute = 100;

  @Parameter(
      names = {"--httpAutoRetries"},
      description =
          "Number of times to retry http requests before queueing, set to 0 to disable (default: 3)")
  int httpAutoRetries = 3;

  @Parameter(
      names = {"--preprocessorConfigFile"},
      description =
          "Optional YAML file with additional configuration options for filtering and pre-processing points")
  String preprocessorConfigFile = null;

  @Parameter(
      names = {"--dataBackfillCutoffHours"},
      description =
          "The cut-off point for what is considered a valid timestamp for back-dated points. Default is 8760 (1 year)")
  int dataBackfillCutoffHours = 8760;

  @Parameter(
      names = {"--dataPrefillCutoffHours"},
      description =
          "The cut-off point for what is considered a valid timestamp for pre-dated points. Default is 24 (1 day)")
  int dataPrefillCutoffHours = 24;

  @Parameter(
      names = {"--authMethod"},
      converter = ProxyConfig.TokenValidationMethodConverter.class,
      description =
          "Authenticate all incoming HTTP requests and disables TCP streams when set to a value "
              + "other than NONE. Allowed values are: NONE, STATIC_TOKEN, HTTP_GET, OAUTH2. Default: NONE")
  TokenValidationMethod authMethod = TokenValidationMethod.NONE;

  @Parameter(
      names = {"--authTokenIntrospectionServiceUrl"},
      description =
          "URL for the token introspection endpoint "
              + "used to validate tokens for incoming HTTP requests. Required for authMethod = OAUTH2 (endpoint must be "
              + "RFC7662-compliant) and authMethod = HTTP_GET (use {{token}} placeholder in the URL to pass token to the "
              + "service, endpoint must return any 2xx status for valid tokens, any other response code is a fail)")
  String authTokenIntrospectionServiceUrl = null;

  @Parameter(
      names = {"--authTokenIntrospectionAuthorizationHeader"},
      description = "Optional credentials for use " + "with the token introspection endpoint.")
  String authTokenIntrospectionAuthorizationHeader = null;

  @Parameter(
      names = {"--authResponseRefreshInterval"},
      description =
          "Cache TTL (in seconds) for token validation "
              + "results (re-authenticate when expired). Default: 600 seconds")
  int authResponseRefreshInterval = 600;

  @Parameter(
      names = {"--authResponseMaxTtl"},
      description =
          "Maximum allowed cache TTL (in seconds) for token "
              + "validation results when token introspection service is unavailable. Default: 86400 seconds (1 day)")
  int authResponseMaxTtl = 86400;

  @Parameter(
      names = {"--authStaticToken"},
      description =
          "Static token that is considered valid "
              + "for all incoming HTTP requests. Required when authMethod = STATIC_TOKEN.")
  String authStaticToken = null;

  @Parameter(
      names = {"--adminApiListenerPort"},
      description = "Enables admin port to control " + "healthcheck status per port. Default: none")
  int adminApiListenerPort = 0;

  @Parameter(
      names = {"--adminApiRemoteIpAllowRegex"},
      description = "Remote IPs must match " + "this regex to access admin API")
  String adminApiRemoteIpAllowRegex = null;

  @Parameter(
      names = {"--httpHealthCheckPorts"},
      description =
          "Comma-delimited list of ports "
              + "to function as standalone healthchecks. May be used independently of "
              + "--httpHealthCheckAllPorts parameter. Default: none")
  String httpHealthCheckPorts = null;

  @Parameter(
      names = {"--httpHealthCheckAllPorts"},
      description =
          "When true, all listeners that "
              + "support HTTP protocol also respond to healthcheck requests. May be used independently of "
              + "--httpHealthCheckPorts parameter. Default: false",
      arity = 1)
  boolean httpHealthCheckAllPorts = false;

  @Parameter(
      names = {"--httpHealthCheckPath"},
      description = "Healthcheck's path, for example, " + "'/health'. Default: '/'")
  String httpHealthCheckPath = "/";

  @Parameter(
      names = {"--httpHealthCheckResponseContentType"},
      description =
          "Optional "
              + "Content-Type to use in healthcheck response, for example, 'application/json'. Default: none")
  String httpHealthCheckResponseContentType = null;

  @Parameter(
      names = {"--httpHealthCheckPassStatusCode"},
      description = "HTTP status code for " + "'pass' health checks. Default: 200")
  int httpHealthCheckPassStatusCode = 200;

  @Parameter(
      names = {"--httpHealthCheckPassResponseBody"},
      description =
          "Optional response " + "body to return with 'pass' health checks. Default: none")
  String httpHealthCheckPassResponseBody = null;

  @Parameter(
      names = {"--httpHealthCheckFailStatusCode"},
      description = "HTTP status code for " + "'fail' health checks. Default: 503")
  int httpHealthCheckFailStatusCode = 503;

  @Parameter(
      names = {"--httpHealthCheckFailResponseBody"},
      description =
          "Optional response " + "body to return with 'fail' health checks. Default: none")
  String httpHealthCheckFailResponseBody = null;

  @Parameter(
      names = {"--deltaCountersAggregationIntervalSeconds"},
      description = "Delay time for delta counter reporter. Defaults to 30 seconds.")
  long deltaCountersAggregationIntervalSeconds = 30;

  @Parameter(
      names = {"--deltaCountersAggregationListenerPorts"},
      description =
          "Comma-separated list of ports to listen on Wavefront-formatted delta "
              + "counters. Helps reduce outbound point rate by pre-aggregating delta counters at proxy."
              + " Defaults: none")
  String deltaCountersAggregationListenerPorts = "";

  @Parameter(
      names = {"--customTimestampTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the timestamp in Wavefront in the absence of a tag named "
              + "`timestamp` or `log_timestamp`. Default: none")
  String customTimestampTags = "";

  @Parameter(
      names = {"--customMessageTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the source in Wavefront in the absence of a tag named "
              + "`message` or `text`. Default: none")
  String customMessageTags = "";

  @Parameter(
      names = {"--customApplicationTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the application in Wavefront in the absence of a tag named "
              + "`application`. Default: none")
  String customApplicationTags = "";

  @Parameter(
      names = {"--customServiceTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the service in Wavefront in the absence of a tag named "
              + "`service`. Default: none")
  String customServiceTags = "";

  @Parameter(
      names = {"--customExceptionTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the exception in Wavefront in the absence of a "
              + "tag named `exception`. Default: exception, error_name")
  String customExceptionTags = "";

  @Parameter(
      names = {"--customLevelTags"},
      description =
          "Comma separated list of log tag "
              + "keys that should be treated as the log level in Wavefront in the absence of a "
              + "tag named `level`. Default: level, log_level")
  String customLevelTags = "";
}
