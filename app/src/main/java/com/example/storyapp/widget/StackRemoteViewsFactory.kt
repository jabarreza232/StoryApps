package com.example.storyapp.widget

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.storyapp.R
import com.example.storyapp.di.Injection
import com.example.storyapp.model.StoryModel
import com.example.storyapp.repository.StoryRepository
import com.google.gson.Gson


class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private var stories: List<StoryModel> = mutableListOf()


    private lateinit var storyRepository: StoryRepository
    override fun onCreate() {
        storyRepository = Injection.provideRepository(mContext)
    }

    override fun onDataSetChanged() {

        val identityToken = Binder.clearCallingIdentity()
        stories = storyRepository.getStoriesWidget()
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = stories.size


    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.item_widget_stories)
        rv.setTextViewText(R.id.nameTextView, stories[position].name)
        rv.setTextViewText(R.id.descriptionTextView, stories[position].description)


        val fillIntent = Intent()
        fillIntent.putExtra(StoryWidget.EXTRA_ITEM, Gson().toJson(stories[position]))
        rv.setOnClickFillInIntent(R.id.fl_widget_stories, fillIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}