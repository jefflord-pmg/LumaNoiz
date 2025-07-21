package com.lusion.noizs

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs

/**
 * Ball counter logic test to ensure no off-by-one errors
 */
class BallCounterTest {

    @Test
    fun testBallCounterLogic() {
        // Simulate the ball counter logic from LightsActivity
        var ballCount = 0
        var lastHorizontalBounce = 0f
        var lastVerticalBounce = 0f
        val ballRadius = 25f // Half of default ball size (50)
        val screenWidth = 800f
        val screenHeight = 1200f

        // Test case 1: Ball bouncing on left boundary
        var ballX = 0f // Ball hits left boundary
        val currentBounceX = ballX
        if (abs(currentBounceX - lastHorizontalBounce) > ballRadius) {
            ballCount++
            lastHorizontalBounce = currentBounceX
        }
        assertEquals("After first left bounce", 1, ballCount)

        // Test case 2: Multiple checks at the same boundary (should not increment)
        // This simulates what happens in rapid animation loops
        for (i in 1..5) {
            ballX = ballRadius // Ball is still at boundary
            val currentBounceXLoop = ballX
            if (abs(currentBounceXLoop - lastHorizontalBounce) > ballRadius) {
                ballCount++
                lastHorizontalBounce = currentBounceXLoop
            }
        }
        assertEquals("Multiple boundary checks should not increment counter", 1, ballCount)

        // Test case 3: Ball moves to right boundary (should increment)
        ballX = screenWidth - ballRadius // Ball hits right boundary
        val currentBounceXRight = ballX
        if (abs(currentBounceXRight - lastHorizontalBounce) > ballRadius) {
            ballCount++
            lastHorizontalBounce = currentBounceXRight
        }
        assertEquals("After right bounce", 2, ballCount)

        // Test case 4: Vertical bounces
        var ballY = 0f // Ball hits top boundary
        val currentBounceYTop = ballY
        if (abs(currentBounceYTop - lastVerticalBounce) > ballRadius) {
            ballCount++
            lastVerticalBounce = currentBounceYTop
        }
        assertEquals("After top bounce", 3, ballCount)

        ballY = screenHeight - ballRadius // Ball hits bottom boundary
        val currentBounceYBottom = ballY
        if (abs(currentBounceYBottom - lastVerticalBounce) > ballRadius) {
            ballCount++
            lastVerticalBounce = currentBounceYBottom
        }
        assertEquals("After bottom bounce", 4, ballCount)

        // Test case 5: Reset for new session
        ballCount = 0
        lastHorizontalBounce = 0f
        lastVerticalBounce = 0f
        assertEquals("After reset", 0, ballCount)
    }

    @Test
    fun testBallCounterEdgeCases() {
        var ballCount = 0
        var lastHorizontalBounce = 0f
        val ballRadius = 25f

        // Test case: Ball position exactly at ball radius distance
        var ballX = ballRadius
        val currentBounceX = ballX
        if (abs(currentBounceX - lastHorizontalBounce) > ballRadius) {
            ballCount++
            lastHorizontalBounce = currentBounceX
        }
        assertEquals("Ball at exact radius distance should increment", 1, ballCount)

        // Test case: Ball position less than ball radius distance
        ballX = ballRadius - 1f
        val currentBounceXClose = ballX
        if (abs(currentBounceXClose - lastHorizontalBounce) > ballRadius) {
            ballCount++
            lastHorizontalBounce = currentBounceXClose
        }
        assertEquals("Ball within radius distance should not increment", 1, ballCount)
    }
}