package com.lusion.lumanoiz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lusion.lumanoiz.ui.theme.LumaNoizAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LightsActivityInstructionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lightsActivity_showsInstructionsOnStart() {
        // Start the screen
        composeTestRule.setContent {
            LumaNoizAppTheme {
                LightsScreen()
            }
        }

        // Verify the instruction dialog is shown
        composeTestRule.onNodeWithText("LumaNoiz - Lights").assertIsDisplayed()
        
        // Verify the exact instruction text from the issue is present
        composeTestRule.onNodeWithText(
            "You will see gently moving light to help relax your mind by stimulating calm brainwave activity through closed eyelids. Simply close your eyes and let the soft motion guide your focus.",
            substring = true
        ).assertIsDisplayed()
        
        composeTestRule.onNodeWithText(
            "A light will slowly travel left and right across the screen. Your goal is to count how many times the light passes in front of your closed eyes.",
            substring = true
        ).assertIsDisplayed()
        
        composeTestRule.onNodeWithText(
            "You can hold anywhere on the screen (long press) to pause the light and view settings, time remaining, and how many passes you've counted so far.",
            substring = true
        ).assertIsDisplayed()
        
        composeTestRule.onNodeWithText(
            "It's a simple, soothing game of counting electric sheep. Relax, breathe, and enjoy the rhythm.",
            substring = true
        ).assertIsDisplayed()

        // Verify the Start button is present
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }

    @Test
    fun lightsActivity_startsGameAfterClickingStart() {
        composeTestRule.setContent {
            LumaNoizAppTheme {
                LightsScreen()
            }
        }

        // Click the Start button
        composeTestRule.onNodeWithText("Start").performClick()

        // Verify the instructions dialog is dismissed
        composeTestRule.onNodeWithText("LumaNoiz - Lights").assertDoesNotExist()
        
        // Verify the game screen is shown
        composeTestRule.onNodeWithText("Moving light will go here...", substring = true).assertIsDisplayed()
    }
}