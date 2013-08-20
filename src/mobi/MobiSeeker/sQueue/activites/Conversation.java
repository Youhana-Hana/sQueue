package mobi.MobiSeeker.sQueue.activites;

import java.util.ArrayList;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Message;
import mobi.MobiSeeker.sQueue.data.Message.MessageType;
import mobi.MobiSeeker.sQueue.data.IMessageSender;
import mobi.MobiSeeker.sQueue.data.MessageAdapter;
import mobi.MobiSeeker.sQueue.data.Messages;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class Conversation extends ListFragment implements IMessageSender {

	private ImageView send = null;
	private Messages messages = null;
	private MessageAdapter adapter = null;
	private ArrayList<Entry> entries = null;
	private ArrayList<Message> messagesList;
	private EditText content;
	private Settings settings;
	private Entry entry;
	private String nodeName;
	private Context context;

	public Conversation() {
		this.entries = new ArrayList<Entry>();
		this.messages = new Messages();
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.context = this.getActivity().getBaseContext();
			this.settings = new Settings(this.context);

			this.entry = new Entry(this.context.getString(R.string.me),
					this.nodeName, this.settings.getLogo(), null);

			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.conversation, container,
				false);

		this.content = (EditText) rootView.findViewById(R.id.message_content);
		this.send = (ImageView) rootView.findViewById(R.id.send);
		this.send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendContent();
			}
		});

		this.send.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return showActions(v);
			}
		});

		return rootView;
	}

	private void sendContent() {
		String content = this.content.getText().toString();
		if (content.isEmpty()) {
			return;
		}

		Message message = new Message(this.entry, content,
				this.settings.getLogo(), MessageType.text);

		this.sendMessage(message);
	}

	@Override
	public void sendMessage(Message message) {
		this.addLocalMessage(message);
		this.sendRemoteMessage(message);
	}

	private void addLocalMessage(Message message) {
		this.messagesList.add(message);
		this.adapter.notifyDataSetChanged();
	}

	private void sendRemoteMessage(Message message) {
		for (Entry entry : this.entries) {
			// send message
		}
	}

	public void sendFile(String fileName) {
		for (Entry entry : this.entries) {
			// send message
		}
	}

	public void addRemoteMessage(Message message) {
		if (message.getType() == MessageType.text) {
			this.messagesList.add(message);
			this.adapter.notifyDataSetChanged();
		} else if (message.getType() == MessageType.RequestImage) {
			requestImage(message);
		} else if (message.getType() == MessageType.RequestVideo) {
			requestVideo(message);
		}
	}

	private String getEntryName(Entry entry) {
		String tabNme = entry.getName().isEmpty() ? entry.getNodeName() : entry
				.getName();
		return tabNme;
	}

	public void requestImage(Message message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(context
				.getString(R.string.request_image_alert_title));
		alertDialog.setMessage(getEntryName(message.getFrom())
				+ context.getString(R.string.request_image_alert_text));
		alertDialog.setPositiveButton(
				this.getResources().getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						captureCamera();
					}

					private void captureCamera() {
						Intent cameraIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent,
								Queue.CAMERA_REQUEST);
					}
				});

		alertDialog.setNegativeButton(
				this.getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Message message = new Message(entry, context
								.getString(R.string.ask_for_image_declined),
								settings.getLogo(), null);
						sendMessage(message);
					}
				});

		alertDialog.create().show();
	}

	public void requestVideo(Message message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(context
				.getString(R.string.request_video_alert_title));
		alertDialog.setMessage(getEntryName(message.getFrom())
				+ context.getString(R.string.request_video_alert_text));
		alertDialog.setPositiveButton(
				this.getResources().getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						captureCamera();
					}

					private void captureCamera() {
						Intent cameraIntent = new Intent(
								android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
						startActivityForResult(cameraIntent,
								Queue.CAMERA_REQUEST);
					}
				});

		alertDialog.setNegativeButton(
				this.getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Message message = new Message(entry, context
								.getString(R.string.ask_for_video_declined),
								settings.getLogo(), null);
						sendMessage(message);
					}
				});

		alertDialog.create().show();
	}

	public void addEntry(Entry entry) {
		this.entries.add(entry);
	}

	public boolean isEntryHere(Entry entry) {
		for (Entry item : this.entries) {
			if (item.getName().equalsIgnoreCase(entry.getName())) {
				return true;
			}
		}

		return false;
	}

	private boolean showActions(View view) {
		DemoPopupWindow dw = new DemoPopupWindow(this.getActivity()
				.getBaseContext(), view, this.entry, this.settings, this);
		dw.showLikeQuickAction(0, 30);
		return true;
	}

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.messagesList = this.messages.getEntries();
		this.adapter = new MessageAdapter(context, R.layout.message,
				this.messagesList);
		setListAdapter(this.adapter);
	}

	public int getMessagesCount() {
		return this.messagesList.size();
	}

	private static class DemoPopupWindow extends BetterPopupWindow implements
			OnClickListener {

		private IMessageSender messageSender = null;
		Context context = null;
		Entry entry = null;
		Settings settings = null;

		public DemoPopupWindow(Context context, View anchor, Entry entry,
				Settings settings, IMessageSender messageSender) {
			super(anchor);
			this.context = context;
			this.entry = entry;
			this.settings = settings;
			this.messageSender = messageSender;
		}

		@Override
		protected void onCreate() {
			// inflate layout
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup root = (ViewGroup) inflater.inflate(
					R.layout.popup_actions_layout, null);

			// setup button events
			for (int i = 0, icount = root.getChildCount(); i < icount; i++) {
				View item = root.getChildAt(i);
				if (item instanceof ImageView) {
					ImageView imageView = (ImageView) item;
					imageView.setOnClickListener(this);
				}
			}

			// set the inflated view as what we want to display
			this.setContentView(root);
		}

		@Override
		public void onClick(View v) {
			ImageView imageView = (ImageView) v;
			if (imageView.getId() == R.id.camera) {
				this.sendCameraRequest();

			} else if (imageView.getId() == R.id.video) {
				this.sendVideoRequest();
			}

			this.dismiss();
		}

		private void sendCameraRequest() {
			Message message = new Message(this.entry,
					this.context.getString(R.string.ask_for_image),
					this.settings.getLogo(), MessageType.RequestImage);
			this.messageSender.sendMessage(message);
		}

		private void sendVideoRequest() {
			Message message = new Message(this.entry,
					this.context.getString(R.string.ask_for_video),
					this.settings.getLogo(), MessageType.RequestVideo);
			this.messageSender.sendMessage(message);
		}
	}

}
