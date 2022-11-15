package com.example.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.storyapp.R
import com.example.storyapp.model.StoryModel
import com.example.storyapp.view.DetailStoryActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StoryWidget : AppWidgetProvider() {
    companion object {
        const val ACTIVITY_ACTION = "com.example.storyapp.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.storyapp.EXTRA_ITEM"
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != null) {
            if (intent.action == ACTIVITY_ACTION) {
                val jsonStoryModel = intent.getStringExtra(EXTRA_ITEM)
                val intent1 = Intent(context, DetailStoryActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val type = object : TypeToken<StoryModel>() {}.type
                val storyModel: StoryModel = Gson().fromJson(jsonStoryModel, type)
                intent1.putExtra("Story", storyModel)
                context.startActivity(intent1)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, StackWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val views = RemoteViews(context.packageName, R.layout.list_story_widget)
        views.setEmptyView(R.id.list_stories, R.id.empty_view)
        views.setRemoteAdapter(R.id.list_stories, intent)


        val toastIntent = Intent(context, StoryWidget::class.java)
        toastIntent.action = ACTIVITY_ACTION
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val toastPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toastIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else 0
        )
        views.setPendingIntentTemplate(R.id.list_stories, toastPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

