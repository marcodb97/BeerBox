package com.marcodallaba.beerbox.ui


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcodallaba.beerbox.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AmberAleTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun amberAleTest() {
        val chip = onView(
            allOf(
                withText("Amber Ale"),
                childAtPosition(
                    allOf(
                        withId(R.id.filterChipGroup),
                        childAtPosition(
                            withId(R.id.chipScrollView),
                            0
                        )
                    ),
                    0
                )
            )
        )
        chip.perform(scrollTo(), click())

        val textView = onView(
            allOf(
                withId(R.id.titleTextView), withText("Devine Rebel (w/ Mikkeller)"),
                withParent(withParent(withId(R.id.recyclerView))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Devine Rebel (w/ Mikkeller)")))

        val textView2 = onView(
            allOf(
                withId(R.id.tagLine), withText("Oak-aged Barley Wine."),
                withParent(withParent(withId(R.id.recyclerView))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Oak-aged Barley Wine.")))

        val textView3 = onView(
            allOf(
                withId(R.id.description),
                withText("Two of Europe's most experimental, boundary-pushing brewers, BrewDog and Mikkeller, combined forces to produce a rebellious beer that combined their respective talents and brewing skills. The 12.5% Barley Wine fermented well, and the champagne yeast drew it ever closer to 12.5%. The beer was brewed with a single hop variety and was going to be partially aged in oak casks."),
                withParent(withParent(withId(R.id.recyclerView))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Two of Europe's most experimental, boundary-pushing brewers, BrewDog and Mikkeller, combined forces to produce a rebellious beer that combined their respective talents and brewing skills. The 12.5% Barley Wine fermented well, and the champagne yeast drew it ever closer to 12.5%. The beer was brewed with a single hop variety and was going to be partially aged in oak casks.")))

        val textView4 = onView(
            allOf(
                withId(R.id.moreInfo), withText("MORE INFO"),
                withParent(withParent(withId(R.id.recyclerView))),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("MORE INFO")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
