package com.uade.back.service.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.uade.back.dto.image.*;

/**
 * Service interface for managing images.
 */
public interface ImageService {
  /**
   * Uploads an image.
   *
   * @param file The image file.
   * @param meta The metadata including product ID.
   * @return The uploaded image response containing the image ID.
   */
  ImageResponse upload(MultipartFile file, ImageUploadRequest meta);

  /**
   * Downloads an image.
   *
   * @param request The image ID request.
   * @return The image resource.
   */
  Resource download(ImageIdRequest request);

  /**
   * Deletes an image.
   *
   * @param request The image ID request.
   */
  void delete(ImageIdRequest request);
}
