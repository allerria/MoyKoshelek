package ru.yandex.moykoshelek

import android.support.test.espresso.Espresso.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.yandex.moykoshelek.ui.main.MainActivity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.ViewPagerActions
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val activityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, true)

    private val testString = "test string"

    @Test
    fun createWallet() {
        onView(withId(R.id.action_add)).perform(click())
        onView(withText(getResourceString(R.string.wallet))).perform(click())
        onView(withId(R.id.wallet_name)).perform(replaceText(testString))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.submit_button)).perform(click())

        //check created wallet
        onView(withId(R.id.cards_viewpager))
                .perform(ViewPagerActions.scrollToLast())
                .check(matches(hasDescendant(withText(testString))))
    }

    @Test
    fun createTransaction() {
        onView(withId(R.id.action_add)).perform(click())
        onView(withText(getResourceString(R.string.income_expense))).perform(click())
        onView(withId(R.id.cards_viewpager)).perform(ViewPagerActions.scrollToLast())
        onView(withId(R.id.transaction_category)).perform(click())
        onView(withText(getResourceString(R.string.cafe_and_restaurants))).perform(click())
        onView(withId(R.id.transaction_amount)).perform(replaceText("555"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.submit_button)).perform(click())

        //check created transaction
        onView(withId(R.id.cards_viewpager)).perform(ViewPagerActions.scrollToLast())
        onView(withId(R.id.transaction_rv))
                .check(matches(hasDescendant(withText(R.string.cafe_and_restaurants))))
    }

    private fun getResourceString(id: Int): String {
        val targetContext = InstrumentationRegistry.getTargetContext()
        return targetContext.resources.getString(id)
    }
}