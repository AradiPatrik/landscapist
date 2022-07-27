/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")
@file:JvmName("FrescoImage")
@file:JvmMultifileClass

package com.skydoves.landscapist.fresco

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.palette.graphics.Palette
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.imagepipeline.request.ImageRequest
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.CircularRevealImage
import com.skydoves.landscapist.ImageBySource
import com.skydoves.landscapist.ImageLoad
import com.skydoves.landscapist.ImageLoadState
import com.skydoves.landscapist.Shimmer
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.palette.BitmapPalette
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Requests loading an image with a loading placeholder and error image resource ([ImageBitmap], [ImageVector], [Painter]).
 *
 * ```
 * FrescoImage(
 *   imageUrl = stringImageUrl,
 *   placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),
 *   error = ImageBitmap.imageResource(R.drawable.error)
 * )
 * ```
 *
 * or we can use [ImageVector] or custom [Painter] like the below.
 *
 * ```
 * placeHolder = ImageVector.vectorResource(R.drawable.placeholder)
 * error = ImageVector.vectorResource(R.drawable.error)
 * ```
 *
 * @param imageUrl The target url to request image.
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param imageRequest The pipeline has to know about requested image to proceed.
 * @param alignment The alignment parameter used to place the loaded [ImageBitmap] in the image container.
 * @param alpha The alpha parameter used to apply for the image when it is rendered onscreen.
 * @param contentScale The scale parameter used to determine the aspect ratio scaling to be
 * used for the loaded [ImageBitmap].
 * @param contentDescription The content description used to provide accessibility to describe the image.
 * @param circularReveal circular reveal parameters for running reveal animation when images are successfully loaded.
 * @param bitmapPalette A [Palette] generator for extracting major (theme) colors from images.
 * @param colorFilter The colorFilter parameter used to apply for the image when it is rendered onscreen.
 * @param placeHolder An [ImageBitmap], [ImageVector], or [Painter] to be displayed when the request is in progress.
 * @param error An [ImageBitmap], [ImageVector], or [Painter] for showing instead of the target image when images are failed to load.
 * @param previewPlaceholder Drawable resource ID which will be displayed when this function is ran in preview mode.
 */
@Composable
public fun FrescoImage(
  imageUrl: String?,
  modifier: Modifier = Modifier,
  imageRequest: @Composable () -> ImageRequest = {
    LocalFrescoProvider.getFrescoImageRequest(imageUrl)
  },
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Crop,
  contentDescription: String? = null,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null,
  circularReveal: CircularReveal? = null,
  bitmapPalette: BitmapPalette? = null,
  placeHolder: Any? = null,
  error: Any? = null,
  observeLoadingProcess: Boolean = false,
  @DrawableRes previewPlaceholder: Int = 0
) {
  FrescoImage(
    imageUrl = imageUrl,
    imageRequest = imageRequest,
    modifier = modifier,
    alignment = alignment,
    contentScale = contentScale,
    contentDescription = contentDescription,
    colorFilter = colorFilter,
    alpha = alpha,
    circularReveal = circularReveal,
    bitmapPalette = bitmapPalette,
    observeLoadingProcess = observeLoadingProcess,
    previewPlaceholder = previewPlaceholder,
    loading = {
      placeHolder?.let {
        ImageBySource(
          source = it,
          modifier = Modifier.matchParentSize(),
          alignment = alignment,
          contentDescription = contentDescription,
          contentScale = contentScale,
          colorFilter = colorFilter,
          alpha = alpha
        )
      }
    },
    failure = {
      error?.let {
        ImageBySource(
          source = it,
          modifier = Modifier.matchParentSize(),
          alignment = alignment,
          contentDescription = contentDescription,
          contentScale = contentScale,
          colorFilter = colorFilter,
          alpha = alpha
        )
      }
    }
  )
}

/**
 * Requests loading an image with a loading an error image resource ([ImageBitmap], [ImageVector], [Painter]).
 *
 * ```
 * FrescoImage(
 *   imageUrl = stringImageUrl,
 *   placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),
 *   error = ImageBitmap.imageResource(R.drawable.error)
 * )
 * ```
 *
 * or we can use [ImageVector] or custom [Painter] like the below.
 *
 * ```
 * error = ImageVector.vectorResource(R.drawable.error)
 * ```
 *
 * @param imageUrl The target url to request image.
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param imageRequest The pipeline has to know about requested image to proceed.
 * @param alignment The alignment parameter used to place the loaded [ImageBitmap] in the image container.
 * @param alpha The alpha parameter used to apply for the image when it is rendered onscreen.
 * @param contentScale The scale parameter used to determine the aspect ratio scaling to be
 * used for the loaded [ImageBitmap].
 * @param contentDescription The content description used to provide accessibility to describe the image.
 * @param circularReveal circular reveal parameters for running reveal animation when images are successfully loaded.
 * @param bitmapPalette A [Palette] generator for extracting major (theme) colors from images.
 * @param colorFilter The colorFilter parameter used to apply for the image when it is rendered onscreen.
 * @param shimmerParams The shimmer related parameter used to determine constructions of the [Shimmer].
 * @param error An [ImageBitmap] for showing instead of the target image when images are failed to load.
 * @param previewPlaceholder Drawable resource ID which will be displayed when this function is ran in preview mode.
 */
@Composable
public fun FrescoImage(
  imageUrl: String?,
  modifier: Modifier = Modifier,
  imageRequest: @Composable () -> ImageRequest = {
    LocalFrescoProvider.getFrescoImageRequest(imageUrl)
  },
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Crop,
  contentDescription: String? = null,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null,
  circularReveal: CircularReveal? = null,
  shimmerParams: ShimmerParams,
  bitmapPalette: BitmapPalette? = null,
  error: ImageBitmap? = null,
  observeLoadingProcess: Boolean = false,
  @DrawableRes previewPlaceholder: Int = 0
) {
  FrescoImage(
    imageUrl = imageUrl,
    imageRequest = imageRequest,
    modifier = modifier,
    alignment = alignment,
    contentScale = contentScale,
    contentDescription = contentDescription,
    colorFilter = colorFilter,
    alpha = alpha,
    circularReveal = circularReveal,
    observeLoadingProcess = observeLoadingProcess,
    shimmerParams = shimmerParams,
    bitmapPalette = bitmapPalette,
    previewPlaceholder = previewPlaceholder,
    failure = {
      error?.let {
        ImageBySource(
          source = it,
          modifier = Modifier.matchParentSize(),
          alignment = alignment,
          contentDescription = contentDescription,
          contentScale = contentScale,
          colorFilter = colorFilter,
          alpha = alpha
        )
      }
    }
  )
}

/**
 * Requests loading an image and create some composables based on [FrescoImageState].
 *
 * ```
 * FrescoImage(
 * imageUrl = stringImageUrl,
 * modifier = modifier,
 * shimmerParams = ShimmerParams (
 *  baseColor = backgroundColor,
 *  highlightColor = highlightColor
 * ),
 * failure = {
 *   Text(text = "image request failed.")
 * })
 * ```
 *
 * @param imageUrl The target url to request image.
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param imageRequest The pipeline has to know about requested image to proceed.
 * @param alignment The alignment parameter used to place the loaded [ImageBitmap] in the image container.
 * @param alpha The alpha parameter used to apply for the image when it is rendered onscreen.
 * @param contentScale The scale parameter used to determine the aspect ratio scaling to be
 * used for the loaded [ImageBitmap].
 * @param contentDescription The content description used to provide accessibility to describe the image.
 * @param circularReveal circular reveal parameters for running reveal animation when images are successfully loaded.
 * @param bitmapPalette A [Palette] generator for extracting major (theme) colors from images.
 * @param colorFilter The colorFilter parameter used to apply for the image when it is rendered onscreen.
 * @param success Content to be displayed when the request is succeeded.
 * @param failure Content to be displayed when the request is failed.
 * @param previewPlaceholder Drawable resource ID which will be displayed when this function is ran in preview mode.
 */
@Composable
public fun FrescoImage(
  imageUrl: String?,
  modifier: Modifier = Modifier,
  imageRequest: @Composable () -> ImageRequest = {
    LocalFrescoProvider.getFrescoImageRequest(imageUrl)
  },
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Crop,
  contentDescription: String? = null,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null,
  circularReveal: CircularReveal? = null,
  observeLoadingProcess: Boolean = false,
  shimmerParams: ShimmerParams,
  bitmapPalette: BitmapPalette? = null,
  @DrawableRes previewPlaceholder: Int = 0,
  success: @Composable (BoxScope.(imageState: FrescoImageState.Success) -> Unit)? = null,
  failure: @Composable (BoxScope.(imageState: FrescoImageState.Failure) -> Unit)? = null
) {
  if (LocalInspectionMode.current && previewPlaceholder != 0) {
    Image(
      modifier = modifier,
      painter = painterResource(id = previewPlaceholder),
      alignment = alignment,
      contentScale = contentScale,
      alpha = alpha,
      colorFilter = colorFilter,
      contentDescription = contentDescription
    )
    return
  }

  FrescoImage(
    recomposeKey = imageUrl,
    imageRequest = imageRequest.invoke(),
    modifier = modifier,
    bitmapPalette = bitmapPalette,
    observeLoadingProcess = observeLoadingProcess
  ) ImageRequest@{ imageState ->
    when (val frescoImageState = imageState.toFrescoImageState()) {
      is FrescoImageState.None -> Unit
      is FrescoImageState.Loading -> {
        Shimmer(
          baseColor = shimmerParams.baseColor,
          highlightColor = shimmerParams.highlightColor,
          intensity = shimmerParams.intensity,
          dropOff = shimmerParams.dropOff,
          tilt = shimmerParams.tilt,
          durationMillis = shimmerParams.durationMillis
        )
      }
      is FrescoImageState.Failure -> failure?.invoke(this, frescoImageState)
      is FrescoImageState.Success -> {
        if (success != null) {
          success.invoke(this, frescoImageState)
        } else {
          val imageBitmap = frescoImageState.imageBitmap ?: return@ImageRequest
          CircularRevealImage(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap,
            alignment = alignment,
            contentScale = contentScale,
            contentDescription = contentDescription,
            alpha = alpha,
            colorFilter = colorFilter,
            circularReveal = circularReveal
          )
        }
      }
    }
  }
}

/**
 * Requests loading an image and create some composables based on [FrescoImageState].
 *
 * ```
 * FrescoImage(
 * imageUrl = stringImageUrl,
 * modifier = modifier,
 * loading = {
 *   Box(modifier = Modifier.matchParentSize()) {
 *     CircularProgressIndicator(
 *        modifier = Modifier.align(Alignment.Center)
 *     )
 *   }
 * },
 * failure = {
 *   Text(text = "image request failed.")
 * })
 * ```
 *
 * @param imageUrl The target url to request image.
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param imageRequest The pipeline has to know about requested image to proceed.
 * @param alignment The alignment parameter used to place the loaded [ImageBitmap] in the image container.
 * @param alpha The alpha parameter used to apply for the image when it is rendered onscreen.
 * @param contentScale The scale parameter used to determine the aspect ratio scaling to be
 * used for the loaded [ImageBitmap].
 * @param contentDescription The content description used to provide accessibility to describe the image.
 * @param circularReveal circular reveal parameters for running reveal animation when images are successfully loaded.
 * @param colorFilter The colorFilter parameter used to apply for the image when it is rendered onscreen.
 * @param loading Content to be displayed when the request is in progress.
 * @param success Content to be displayed when the request is succeeded.
 * @param failure Content to be displayed when the request is failed.
 * @param previewPlaceholder Drawable resource ID which will be displayed when this function is ran in preview mode.
 */
@Composable
public fun FrescoImage(
  imageUrl: String?,
  modifier: Modifier = Modifier,
  imageRequest: @Composable () -> ImageRequest = {
    LocalFrescoProvider.getFrescoImageRequest(imageUrl)
  },
  alignment: Alignment = Alignment.Center,
  contentScale: ContentScale = ContentScale.Crop,
  contentDescription: String? = null,
  alpha: Float = DefaultAlpha,
  colorFilter: ColorFilter? = null,
  circularReveal: CircularReveal? = null,
  bitmapPalette: BitmapPalette? = null,
  observeLoadingProcess: Boolean = false,
  @DrawableRes previewPlaceholder: Int = 0,
  loading: @Composable (BoxScope.(imageState: FrescoImageState.Loading) -> Unit)? = null,
  success: @Composable (BoxScope.(imageState: FrescoImageState.Success) -> Unit)? = null,
  failure: @Composable (BoxScope.(imageState: FrescoImageState.Failure) -> Unit)? = null
) {
  if (LocalInspectionMode.current && previewPlaceholder != 0) {
    Image(
      modifier = modifier,
      painter = painterResource(id = previewPlaceholder),
      alignment = alignment,
      contentScale = contentScale,
      alpha = alpha,
      colorFilter = colorFilter,
      contentDescription = contentDescription
    )
    return
  }

  FrescoImage(
    recomposeKey = imageUrl,
    imageRequest = imageRequest.invoke(),
    modifier = modifier,
    bitmapPalette = bitmapPalette,
    observeLoadingProcess = observeLoadingProcess
  ) ImageRequest@{ imageState ->
    when (val frescoImageState = imageState.toFrescoImageState()) {
      is FrescoImageState.None -> Unit
      is FrescoImageState.Loading -> loading?.invoke(this, frescoImageState)
      is FrescoImageState.Failure -> failure?.invoke(this, frescoImageState)
      is FrescoImageState.Success -> {
        if (success != null) {
          success.invoke(this, frescoImageState)
        } else {
          val imageBitmap = frescoImageState.imageBitmap ?: return@ImageRequest
          CircularRevealImage(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap,
            alignment = alignment,
            contentScale = contentScale,
            contentDescription = contentDescription,
            alpha = alpha,
            colorFilter = colorFilter,
            circularReveal = circularReveal
          )
        }
      }
    }
  }
}

/**
 * Requests loading an image and create a composable that provides
 * the current state [ImageLoadState] of the content.
 *
 * ```
 * FrescoImage(
 * imageUri = Uri.parse(stringImageUrl),
 * modifier = modifier,
 * imageRequest = imageRequest
 * ) { imageState ->
 *   when (val frescoImageState = imageState.toFrescoImageState()) {
 *     is FrescoImageState.None -> // do something
 *     is FrescoImageState.Loading -> // do something
 *     is FrescoImageState.Failure -> // do something
 *     is FrescoImageState.Success ->  // do something
 *   }
 * }
 * ```
 *
 * @param imageRequest The pipeline has to know about requested image to proceed.
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param bitmapPalette A [Palette] generator for extracting major (theme) colors from images.
 * @param content Content to be displayed for the given state.
 */
@Composable
private fun FrescoImage(
  recomposeKey: Any?,
  imageRequest: ImageRequest,
  modifier: Modifier = Modifier,
  bitmapPalette: BitmapPalette? = null,
  observeLoadingProcess: Boolean = false,
  content: @Composable BoxScope.(imageState: ImageLoadState) -> Unit
) {
  val context = LocalContext.current
  val datasource =
    remember(recomposeKey) { imagePipeline.fetchDecodedImage(imageRequest, context) }

  ImageLoad(
    recomposeKey = recomposeKey,
    executeImageRequest = {
      suspendCancellableCoroutine { cont ->
        val subscriber = FlowBaseBitmapDataSubscriber(
          observeLoadingProcess,
          bitmapPalette?.applyImageModel(recomposeKey)
        )
        datasource.subscribe(subscriber, CallerThreadExecutor.getInstance())

        cont.resume(subscriber.imageLoadStateFlow) {
          // close the fresco datasource request if cancelled.
          datasource.close()
        }
      }
    },
    modifier = modifier,
    content = content
  )
}
