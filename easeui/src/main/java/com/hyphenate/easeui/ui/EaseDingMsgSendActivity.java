package com.hyphenate.easeui.ui;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.EaseToolbar;

public class EaseDingMsgSendActivity extends EaseBaseActivity {

    private EaseToolbar toolbar;
    private EditText msgEidtText;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_acitivity_ding_msg_send;
    }

    @Override
    protected void initView() {
        super.initView();
        toolbar = findViewById(R.id.toolbar);
        msgEidtText = findViewById(R.id.et_sendmessage);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ease_menu_ding_msg_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ease_action_send) {
            String msgContent = msgEidtText.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("msg", msgContent);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}