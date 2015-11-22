package com.happy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.happy.manage.MediaManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;

public class PhoneReceiver extends BroadcastReceiver {

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("android.intent.action.MEDIA_BUTTON")) {
			this.context = context;
			// 耳机事件 Intent 附加值为(Extra)点击MEDIA_BUTTON的按键码

			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (event == null)
				return;

			boolean isActionUp = (event.getAction() == KeyEvent.ACTION_UP);
			if (!isActionUp)
				return;

			int keyCode = event.getKeyCode();
			long eventTime = event.getEventTime() - event.getDownTime();// 按键按下到松开的时长

			Message msg = Message.obtain();
			msg.what = 100;
			Bundle data = new Bundle();
			data.putInt("key_code", keyCode);
			data.putLong("event_time", eventTime);
			msg.setData(data);
			phoneHandler.sendMessage(msg);
		}
		// 终止广播(不让别的程序收到此广播，免受干扰)
		// abortBroadcast();
	}

	/**
	 * 耳机处理
	 */
	private Handler phoneHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 100:// 单击按键广播
				Bundle data = msg.getData();
				// 按键值
				int keyCode = data.getInt("key_code");
				// 按键时长
				long eventTime = data.getLong("event_time");
				// 设置超过10毫秒，就触发长按事件
				boolean isLongPress = (eventTime > 10);

				switch (keyCode) {
				case KeyEvent.KEYCODE_HEADSETHOOK:// 播放或暂停
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:// 播放或暂停
					playOrPause();
					break;

				// 短按=播放下一首音乐，长按=当前音乐快进
				case KeyEvent.KEYCODE_MEDIA_NEXT:
					if (isLongPress) {
						fastNext(50 * 1000);// 自定义
					} else {
						playNext();// 自定义
					}
					break;

				// 短按=播放上一首音乐，长按=当前音乐快退
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					if (isLongPress) {
						fastPrevious(50 * 1000);// 自定义
					} else {
						playPrevious();// 自定义
					}
					break;
				}

				break;
			// 快进
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
				fastNext(10 * 1000);// 自定义
				break;
			// 快退
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				fastPrevious(10 * 1000);// 自定义
				break;
			case KeyEvent.KEYCODE_MEDIA_STOP:

				break;
			default:// 其他消息-则扔回上层处理
				super.handleMessage(msg);
			}
		}

		private void fastPrevious(int dProgress) {
			SongInfo tempSongInfo = MediaManage.getMediaManage(context)
					.getSongInfo();
			if (tempSongInfo != null) {
				long progress = tempSongInfo.getPlayProgress();
				long minProgress = 0;
				progress = progress - dProgress;
				if (progress <= minProgress) {
					progress = minProgress;
				}
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress((int) progress);
				ObserverManage.getObserver().setMessage(songMessage);
			}

		}

		private void fastNext(int dProgress) {
			SongInfo tempSongInfo = MediaManage.getMediaManage(context)
					.getSongInfo();
			if (tempSongInfo != null) {
				long progress = tempSongInfo.getPlayProgress();
				long maxProgress = tempSongInfo.getDuration();
				progress = progress + dProgress;
				if (progress >= maxProgress) {
					progress = maxProgress;
				}
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress((int) progress);
				ObserverManage.getObserver().setMessage(songMessage);
			}
		}

		private void playPrevious() {
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.PREMUSIC);
			ObserverManage.getObserver().setMessage(songMessage);
		}

		private void playNext() {
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.NEXTMUSIC);
			ObserverManage.getObserver().setMessage(songMessage);
		}

		private void playOrPause() {
			SongMessage songMessage = new SongMessage();

			if (MediaManage.getMediaManage(context).getPlayStatus() == MediaManage.PLAYING) {
				songMessage.setType(SongMessage.PAUSEMUSIC);
			} else {
				songMessage.setType(SongMessage.PLAYMUSIC);
			}
			ObserverManage.getObserver().setMessage(songMessage);
		}

	};

}
