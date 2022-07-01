package com.wavefront.agent.handlers;

import com.wavefront.agent.buffer.Buffer;
import com.wavefront.agent.buffer.QueueInfo;
import javax.annotation.Nonnull;

/** Factory for {@link SenderTask} objects. */
public interface SenderTaskFactory {

  /**
   * Create a collection of {@link SenderTask objects} for a specified handler key.
   *
   * @param handlerKey unique identifier for the handler.
   * @param level_1
   * @return created tasks corresponding to different Wavefront endpoints {@link
   *     com.wavefront.api.ProxyV2API}.
   */
  void createSenderTasks(@Nonnull QueueInfo info, Buffer level_1);

  /** Shut down all tasks. */
  void shutdown();

  /**
   * Shut down specific pipeline
   *
   * @param handle pipeline's handle
   */
  void shutdown(@Nonnull String handle);

  void truncateBuffers();
}
