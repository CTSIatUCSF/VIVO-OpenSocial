/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.filestorage.updater;

import static edu.cornell.mannlib.vitro.webapp.controller.freemarker.ImageUploadController.THUMBNAIL_HEIGHT;
import static edu.cornell.mannlib.vitro.webapp.controller.freemarker.ImageUploadController.THUMBNAIL_WIDTH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.ImageUploadController.CropRectangle;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.ImageUploadThumbnailer;

/**
 * Adjust any individual that has a main image but no thumbnail.
 */
public class NoThumbsAdjuster extends FsuScanner {
	private ImageDirectoryWithBackup imageDirectoryWithBackup;

	public NoThumbsAdjuster(FSUController controller) {
		super(controller);
		this.imageDirectoryWithBackup = controller
				.getImageDirectoryWithBackup();
	}

	/**
	 * For every individual with main images but no thumbnails, create a
	 * thumbnail from the first main image.
	 */
	public void adjust() {
		updateLog.section("Creating thumbnails to match main images.");

		for (Resource resource : ModelWrapper.listResourcesWithProperty(model,
				imageProperty)) {
			if (resource.getProperty(thumbProperty) == null) {
				createThumbnailFromMainImage(resource);
			}
		}
	}

	/**
	 * This individual has a main image but no thumbnail. Create one.
	 * <ul>
	 * <li>Figure a name for the thumbnail image.</li>
	 * <li>Make a scaled copy of the main image into the thumbnail.</li>
	 * <li>Set that file as a thumbnail (old-style) on the individual.</li>
	 * </ul>
	 */
	private void createThumbnailFromMainImage(Resource resource) {
		String mainFilename = getValues(resource, imageProperty).get(0);
		String thumbFilename = addFilenamePrefix("_thumbnail_", mainFilename);
		updateLog.log(resource, "creating a thumbnail at '" + thumbFilename
				+ "' from the main image at '" + mainFilename + "'");

		File mainFile = imageDirectoryWithBackup.getExistingFile(mainFilename);
		File thumbFile = imageDirectoryWithBackup.getNewfile(thumbFilename);
		thumbFile = checkNameConflicts(thumbFile);

		try {
			CropRectangle crop = getImageSize(mainFile);
			if (imageIsSmallEnoughAlready(crop)) {
				copyMainImageToThumbnail(mainFile, thumbFile);
			} else {
				cropScaleAndStore(crop, mainFile, thumbFile);
			}

			ResourceWrapper.addProperty(resource, thumbProperty, thumbFilename);
		} catch (IOException e) {
			updateLog.error(resource, "failed to create thumbnail file '"
					+ thumbFilename + "'", e);
		}
	}

	private CropRectangle getImageSize(File file) throws IOException {
		InputStream imageSource = null;
		try {
			imageSource = new FileInputStream(file);
			MemoryCacheSeekableStream stream = new MemoryCacheSeekableStream(
					imageSource);
			RenderedOp image = JAI.create("stream", stream);
			return new CropRectangle(0, 0, image.getHeight(), image.getWidth());
		} finally {
			if (imageSource != null) {
				try {
					imageSource.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean imageIsSmallEnoughAlready(CropRectangle crop) {
		return (crop.height <= THUMBNAIL_HEIGHT)
				&& (crop.width <= THUMBNAIL_WIDTH);
	}

	private void copyMainImageToThumbnail(File mainFile, File thumbFile)
			throws IOException {
		InputStream imageSource = null;
		try {
			imageSource = new FileInputStream(mainFile);
			storeImage(imageSource, thumbFile);
		} finally {
			if (imageSource != null) {
				try {
					imageSource.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void cropScaleAndStore(CropRectangle crop, File mainFile,
			File thumbFile) throws IOException {
		InputStream mainImageStream = null;
		InputStream imageSource = null;
		try {
			mainImageStream = new FileInputStream(mainFile);
			ImageUploadThumbnailer iut = new ImageUploadThumbnailer(
					THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
			imageSource = iut.cropAndScale(mainImageStream, crop);
			storeImage(imageSource, thumbFile);
		} finally {
			if (mainImageStream != null) {
				try {
					mainImageStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (imageSource != null) {
				try {
					imageSource.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void storeImage(InputStream source, File file) throws IOException {
		OutputStream sink = null;
		try {
			sink = new FileOutputStream(file);
			
			byte[] buffer = new byte[8192];
			int howMany;
			while (-1 != (howMany = source.read(buffer))) {
				sink.write(buffer, 0, howMany);
			}
		} finally {
			if (sink != null) {
				try {
					sink.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
