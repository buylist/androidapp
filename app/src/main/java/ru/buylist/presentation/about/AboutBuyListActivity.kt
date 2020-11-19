package ru.buylist.presentation.about

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import ru.buylist.R

class AboutBuyListActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setColorDoneText(resources.getColor(R.color.colorBlack))
        setColorSkipButton(resources.getColor(R.color.colorBlack))
        setNextArrowColor(resources.getColor(R.color.colorBlack))
        setIndicatorColor(
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorSlideUnselectedIndicator)
        )

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.slide_welcome_title),
                description = getString(R.string.slide_welcome_main),
                imageDrawable = R.drawable.ic_slide_logo,
                backgroundColor = Color.WHITE,
                titleColor = resources.getColor(R.color.colorSlideTextTitle),
                descriptionColor = resources.getColor(R.color.colorSlideTextMain)
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.slide_list_title),
                description = getString(R.string.slide_list_main),
                imageDrawable = R.drawable.ic_slide_list,
                backgroundColor = Color.WHITE,
                titleColor = resources.getColor(R.color.colorSlideTextTitle),
                descriptionColor = resources.getColor(R.color.colorSlideTextMain)
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.slide_pattern_title),
                description = getString(R.string.slide_pattern_main),
                imageDrawable = R.drawable.ic_slide_pattern,
                backgroundColor = Color.WHITE,
                titleColor = resources.getColor(R.color.colorSlideTextTitle),
                descriptionColor = resources.getColor(R.color.colorSlideTextMain)
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.slide_recipe_title),
                description = getString(R.string.slide_recipe_main),
                imageDrawable = R.drawable.ic_slide_recipe,
                backgroundColor = Color.WHITE,
                titleColor = resources.getColor(R.color.colorSlideTextTitle),
                descriptionColor = resources.getColor(R.color.colorSlideTextMain)
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.slide_product_dictionary_title),
                description = getString(R.string.slide_product_dictionary_main),
                imageDrawable = R.drawable.ic_slide_book,
                backgroundColor = Color.WHITE,
                titleColor = resources.getColor(R.color.colorSlideTextTitle),
                descriptionColor = resources.getColor(R.color.colorSlideTextMain)
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

}