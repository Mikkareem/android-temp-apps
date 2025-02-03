@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.techullurgy.retest.sharedelement

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techullurgy.retest.R

private val boundsTransform = BoundsTransform { _, _ ->
    tween(500)
}

private val ChocolateCosmos = Color(0xff53131e)
private val Garnet = Color(0xff6a3937)
private val TextColor = Color(0xffffffff)
private val PriceColor = Color(0xfffffacc)

@Preview
@Composable
fun SharedElementTransitionScreen1() {

    var selectedSnack by remember { mutableStateOf<Snack1?>(null) }

    SharedTransitionLayout {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ChocolateCosmos)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(snacks) { index, snack ->
                AnimatedVisibility(
                    visible = snack != selectedSnack,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.animateItem()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState("$snack"),
                                animatedVisibilityScope = this,
                                boundsTransform = boundsTransform,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                            .clip(RoundedCornerShape(20))
                            .background(Garnet)
                            .padding(8.dp)
                            .clickable {
                                selectedSnack = snack
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "$snack-image"),
                                    animatedVisibilityScope = this@AnimatedVisibility,
                                    clipInOverlayDuringTransition = OverlayClip(CircleShape),
                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                    boundsTransform = boundsTransform
                                )
                                .size(60.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(snack.image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = snack.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = TextColor,
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "$snack-title"),
                                        animatedVisibilityScope = this@AnimatedVisibility,
                                        boundsTransform = boundsTransform
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = snack.tagName,
                                color = TextColor,
                                modifier = Modifier
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "$snack-tagName"),
                                        animatedVisibilityScope = this@AnimatedVisibility,
                                        boundsTransform = boundsTransform
                                    )
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "$ ${snack.price}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PriceColor,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = "$snack-price"),
                                    animatedVisibilityScope = this@AnimatedVisibility,
                                    boundsTransform = boundsTransform
                                )
                        )
                    }
                }
            }
        }

        SnackDetail(
            snack = selectedSnack,
            sharedTransitionScope = this,
            onBack = {
                selectedSnack = null
            }
        )
    }
}

@Composable
fun SnackDetail(
    snack: Snack1?,
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = snack,
        label = "SnackDetail",
        modifier = modifier.clickable(onClick = onBack)
    ) { targetState ->
        targetState?.let {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.fillMaxSize().background(Color.Black.copy(0.5f))
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState("$it"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = boundsTransform,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                            .wrapContentWidth()
                            .background(Garnet)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "$it-image"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    clipInOverlayDuringTransition = OverlayClip(CircleShape),
                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                    boundsTransform = boundsTransform
                                )
                                .fillMaxWidth(0.8f)
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(it.image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = it.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 21.sp,
                            color = TextColor,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "$it-title"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                        )

                        Text(
                            text = it.tagName,
                            color = TextColor,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = "$it-tagName"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                        )

                        Text(
                            text = it.description,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .skipToLookaheadSize()
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "$ ${it.price}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PriceColor,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = "$it-price"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = boundsTransform
                                )
                        )

                        Spacer(Modifier.height(6.dp))

                        Text("Buy Now", color = Color.Magenta)
                    }
                }
            }
        }
    }
}


data class Snack1(
    val title: String,
    val tagName: String,
    val price: Double,
    val description: String,
    @DrawableRes val image: Int
)

private val snacks = listOf(
    Snack1(
        title = "Cupcake1",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.cupcake
    ),
    Snack1(
        title = "Donut1",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.donut
    ),
    Snack1(
        title = "Eclair1",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.eclair
    ),
    Snack1(
        title = "Gingerbread1",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.gingerbread
    ),
    Snack1(
        title = "Honeycomb1",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.honeycomb
    ),
    Snack1(
        title = "Cupcake2",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.cupcake
    ),
    Snack1(
        title = "Donut2",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.donut
    ),
    Snack1(
        title = "Eclair2",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.eclair
    ),
    Snack1(
        title = "Gingerbread2",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.gingerbread
    ),
    Snack1(
        title = "Honeycomb2",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.honeycomb
    ),
    Snack1(
        title = "Cupcake3",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.cupcake
    ),
    Snack1(
        title = "Donut3",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.donut
    ),
    Snack1(
        title = "Eclair3",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.eclair
    ),
    Snack1(
        title = "Gingerbread3",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.gingerbread
    ),
    Snack1(
        title = "Honeycomb3",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.honeycomb
    ),
    Snack1(
        title = "Cupcake4",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.cupcake
    ),
    Snack1(
        title = "Donut4",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.donut
    ),
    Snack1(
        title = "Eclair4",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.eclair
    ),
    Snack1(
        title = "Gingerbread4",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.gingerbread
    ),
    Snack1(
        title = "Honeycomb4",
        tagName = "A spicy food",
        price = 2367.89,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        image = R.drawable.honeycomb
    ),
)