package org.beem.tastymap.ui.review.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.AppColors


val TastyPinIcon: ImageVector
    get() {
        if (_tastyPinIcon != null) {
            return _tastyPinIcon!!
        }

        _tastyPinIcon = ImageVector.Builder(
            name = "TastyPin",
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {

            /*
             * ─────────────────────────────────────
             * 1. ANA TASTYMAP PIN
             * ─────────────────────────────────────
             *
             * Güçlü ve sade bir harita pini.
             */
            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(24f, 3.5f)

                curveTo(
                    14.4f, 3.5f,
                    6.8f, 11.0f,
                    6.8f, 20.2f
                )

                curveTo(
                    6.8f, 31.7f,
                    19.4f, 42.3f,
                    23.0f, 45.0f
                )

                curveTo(
                    23.6f, 45.5f,
                    24.4f, 45.5f,
                    25.0f, 45.0f
                )

                curveTo(
                    28.6f, 42.3f,
                    41.2f, 31.7f,
                    41.2f, 20.2f
                )

                curveTo(
                    41.2f, 11.0f,
                    33.6f, 3.5f,
                    24f, 3.5f
                )

                close()
            }


            /*
             * ─────────────────────────────────────
             * 2. ALTIN TABAK
             * ─────────────────────────────────────
             *
             * Pin'in ortasındaki yemek dünyasını
             * temsil eden güçlü dairesel alan.
             */
            path(
                fill = SolidColor(AppColors.Gold)
            ) {
                moveTo(24f, 9f)

                curveTo(
                    17.1f, 9f,
                    11.5f, 14.5f,
                    11.5f, 21.3f
                )

                curveTo(
                    11.5f, 28.1f,
                    17.1f, 33.6f,
                    24f, 33.6f
                )

                curveTo(
                    30.9f, 33.6f,
                    36.5f, 28.1f,
                    36.5f, 21.3f
                )

                curveTo(
                    36.5f, 14.5f,
                    30.9f, 9f,
                    24f, 9f
                )

                close()
            }


            /*
             * ─────────────────────────────────────
             * 3. İÇ TABAK
             * ─────────────────────────────────────
             *
             * Beyaz alan ikonu ferahlatıyor.
             */
            path(
                fill = SolidColor(Color.White)
            ) {
                moveTo(24f, 12.8f)

                curveTo(
                    19.2f, 12.8f,
                    15.3f, 16.6f,
                    15.3f, 21.3f
                )

                curveTo(
                    15.3f, 26.0f,
                    19.2f, 29.8f,
                    24f, 29.8f
                )

                curveTo(
                    28.8f, 29.8f,
                    32.7f, 26.0f,
                    32.7f, 21.3f
                )

                curveTo(
                    32.7f, 16.6f,
                    28.8f, 12.8f,
                    24f, 12.8f
                )

                close()
            }


            /*
             * ─────────────────────────────────────
             * 4. ÇATAL
             * ─────────────────────────────────────
             */

            // Çatal sapı
            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(19.0f, 18.0f)
                lineTo(20.7f, 18.0f)
                lineTo(20.7f, 27.0f)
                lineTo(19.0f, 27.0f)
                close()
            }

            // Sol diş
            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(17.2f, 15.7f)
                lineTo(18.5f, 15.7f)
                lineTo(18.5f, 20.0f)

                curveTo(
                    18.5f, 20.8f,
                    19.1f, 21.4f,
                    19.9f, 21.4f
                )

                lineTo(19.9f, 23.0f)

                curveTo(
                    18.3f, 23.0f,
                    17.2f, 21.8f,
                    17.2f, 20.1f
                )

                close()
            }

            // Orta diş
            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(19.2f, 15.7f)
                lineTo(20.5f, 15.7f)
                lineTo(20.5f, 20.3f)
                lineTo(19.2f, 20.3f)
                close()
            }

            // Sağ diş
            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(21.2f, 15.7f)
                lineTo(22.5f, 15.7f)
                lineTo(22.5f, 20.1f)

                curveTo(
                    22.5f, 21.8f,
                    21.4f, 23.0f,
                    19.9f, 23.0f
                )

                lineTo(19.9f, 21.4f)

                curveTo(
                    20.6f, 21.4f,
                    21.2f, 20.8f,
                    21.2f, 20.0f
                )

                close()
            }


            /*
             * ─────────────────────────────────────
             * 5. BIÇAK
             * ─────────────────────────────────────
             */

            path(
                fill = SolidColor(AppColors.NavyBlue)
            ) {
                moveTo(26.2f, 15.7f)

                curveTo(
                    29.2f, 17.0f,
                    30.2f, 19.7f,
                    29.8f, 22.0f
                )

                lineTo(28.3f, 22.0f)
                lineTo(28.3f, 27.0f)
                lineTo(26.6f, 27.0f)
                lineTo(26.6f, 21.0f)

                curveTo(
                    26.6f, 19.8f,
                    26.4f, 17.6f,
                    26.2f, 15.7f
                )

                close()
            }


            /*
             * Küçük Tasty dokunuşu:
             * tabak üzerinde minicik lezzet noktası.
             */
            path(
                fill = SolidColor(AppColors.WarmAmber)
            ) {
                moveTo(24f, 18.2f)

                curveTo(
                    25.0f, 18.2f,
                    25.8f, 19.0f,
                    25.8f, 20.0f
                )

                curveTo(
                    25.8f, 21.0f,
                    25.0f, 21.8f,
                    24f, 21.8f
                )

                curveTo(
                    23.0f, 21.8f,
                    22.2f, 21.0f,
                    22.2f, 20.0f
                )

                curveTo(
                    22.2f, 19.0f,
                    23.0f, 18.2f,
                    24f, 18.2f
                )

                close()
            }
        }.build()

        return _tastyPinIcon!!
    }

private var _tastyPinIcon: ImageVector? = null