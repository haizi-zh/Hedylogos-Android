package com.hedylogos.Activity;

/**
public class PrivateConversationActivity extends Activity
    implements
      OnClickListener,
      MessageListener {
  public static final String DATA_EXTRA_SINGLE_DIALOG_TARGET = "single_target_peerId";

  String targetPeerId;
  private ImageButton sendBtn;
  private EditText composeZone;
  String currentName;
  String selfId;
  ListView chatList;
  ChatDataAdapter adapter;
  List<Message> messages = new LinkedList<Message>();
  //Session session;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.heartbeat);
    targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
    this.setTitle(HTBApplication.lookupname(targetPeerId));

    // 您可以在这里读取本地的聊天记录，并且加载进来。
    // 　我们会在未来加入这些代码

    chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
    adapter = new ChatDataAdapter(this, messages);
    chatList.setAdapter(adapter);
    sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
    composeZone = (EditText) this.findViewById(R.id.chatText);
    selfId =AVUser.getCurrentUser().getObjectId();
    currentName = HTBApplication.lookupname(selfId);
    session = SessionManager.getInstance(selfId);
    sendBtn.setOnClickListener(this);
    if (!AVUtils.isBlankString(getIntent().getExtras()
        .getString(Session.AV_SESSION_INTENT_DATA_KEY))) {
      String msg = getIntent().getExtras().getString(Session.AV_SESSION_INTENT_DATA_KEY);
      Message message = JSON.parseObject(msg, Message.class);
      messages.add(message);
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onClick(View v) {
    String text = composeZone.getText().toString();

    if (TextUtils.isEmpty(text)) {
      return;
    }


    composeZone.getEditableText().clear();
    ChatDemoMessage message = new ChatDemoMessage();
    message.setFromPeerId(selfId);
    message.setMessageContent(text);
    message.setMessageType(MessageType.Text);
    message.setMessageFrom(currentName);
    message.setToPeerIds(Arrays.asList(targetPeerId));
    session.sendMessage(message.makeMessage());

    messages.add(message);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  @Override
  public void onResume() {
    super.onResume();
    ChatDemoMessageReceiver.registerSessionListener(targetPeerId, this);
  }

  @Override
  public void onPause() {
    super.onPause();
    ChatDemoMessageReceiver.unregisterSessionListener(targetPeerId);
  }

  @Override
  public void onMessage(String msg) {
    ChatDemoMessage message = JSON.parseObject(msg, ChatDemoMessage.class);
    messages.add(message);
    adapter.notifyDataSetChanged();
  }
}
**/