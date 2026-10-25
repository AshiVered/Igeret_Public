package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ApiKeysSetupActivity extends BaseFragment {

    private boolean isFromSettings;
    private EditTextBoldCursor appIdField;
    private EditTextBoldCursor appHashField;

    public ApiKeysSetupActivity() {
        this(false);
    }

    public ApiKeysSetupActivity(boolean isFromSettings) {
        super();
        this.isFromSettings = isFromSettings;
    }

    @Override
    public View createView(Context context) {
        actionBar.setTitle(LocaleController.isRTL ? "הגדרת מפתחות Telegram API" : "Telegram API Keys");
        if (isFromSettings) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1 && isFromSettings) {
                    finishFragment();
                }
            }
        });

        FrameLayout contentView = new FrameLayout(context);
        contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        ScrollView scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        contentView.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(20), AndroidUtilities.dp(16), AndroidUtilities.dp(36));
        scrollView.addView(contentLayout, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL));

        // Top Icon
        ImageView iconView = new ImageView(context);
        iconView.setImageResource(R.drawable.msg_permissions);
        iconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), PorterDuff.Mode.SRC_IN));
        contentLayout.addView(iconView, LayoutHelper.createLinear(64, 64, Gravity.CENTER_HORIZONTAL, 0, 8, 0, 16));

        // Title
        TextView titleView = new TextView(context);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setText(LocaleController.isRTL ? "הגדרת מפתחות התחברות אישיים" : "Personal API Credentials");
        contentLayout.addView(titleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 8));

        // Subtitle / explanation
        TextView subtitleView = new TextView(context);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        subtitleView.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitleView.setLineSpacing(AndroidUtilities.dp(3), 1.0f);
        subtitleView.setText(LocaleController.isRTL ?
                "כדי להשתמש באפליקציה באופן עצמאי, פרטי ומאובטח, יש להזין את מפתחות ה-API האישיים שלך מ-Telegram." :
                "To use the app independently and securely, please enter your personal Telegram API credentials.");
        contentLayout.addView(subtitleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 8, 0, 8, 20));

        // Instructions Card
        LinearLayout instructionsCard = new LinearLayout(context);
        instructionsCard.setOrientation(LinearLayout.VERTICAL);
        instructionsCard.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(12), Theme.getColor(Theme.key_windowBackgroundWhite)));
        instructionsCard.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        contentLayout.addView(instructionsCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        TextView instructionsTitle = new TextView(context);
        instructionsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        instructionsTitle.setTypeface(AndroidUtilities.bold());
        instructionsTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        instructionsTitle.setText(LocaleController.isRTL ? "איך מקבלים את המפתחות בחינם ובקלות?" : "How to get your free API keys?");
        instructionsCard.addView(instructionsTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 8));

        TextView instructionsSteps = new TextView(context);
        instructionsSteps.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        instructionsSteps.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        instructionsSteps.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        instructionsSteps.setText(LocaleController.isRTL ?
                "1. היכנס לאתר my.telegram.org והתחבר עם מספר הטלפון שלך.\n" +
                "2. לחץ על 'API development tools'.\n" +
                "3. מלא שם אפליקציה ושם קצר (למשל: MyClient) ולחץ 'Create application'.\n" +
                "4. העתק את ה-App api_id ואת ה-App api_hash והדבק אותם כאן למטה." :
                "1. Log in to my.telegram.org with your phone number.\n" +
                "2. Click on 'API development tools'.\n" +
                "3. Fill in app title and short name, then click 'Create application'.\n" +
                "4. Copy your App api_id and App api_hash and paste them below.");
        instructionsCard.addView(instructionsSteps, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 14));

        // Open Website Button
        TextView openWebButton = new TextView(context);
        openWebButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        openWebButton.setTypeface(AndroidUtilities.bold());
        openWebButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        openWebButton.setGravity(Gravity.CENTER);
        openWebButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        openWebButton.setPadding(AndroidUtilities.dp(14), AndroidUtilities.dp(10), AndroidUtilities.dp(14), AndroidUtilities.dp(10));
        openWebButton.setText(LocaleController.isRTL ? "פתח את my.telegram.org בדפדפן" : "Open my.telegram.org");
        openWebButton.setOnClickListener(v -> {
            Browser.openUrl(getParentActivity(), "https://my.telegram.org");
        });
        instructionsCard.addView(openWebButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        // Form Card
        LinearLayout formCard = new LinearLayout(context);
        formCard.setOrientation(LinearLayout.VERTICAL);
        formCard.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(12), Theme.getColor(Theme.key_windowBackgroundWhite)));
        formCard.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        contentLayout.addView(formCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 24));

        // Field 1: App api_id
        TextView appIdLabel = new TextView(context);
        appIdLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        appIdLabel.setTypeface(AndroidUtilities.bold());
        appIdLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        appIdLabel.setText(LocaleController.isRTL ? "App api_id (מספר)" : "App api_id (numeric)");
        formCard.addView(appIdLabel, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        appIdField = new EditTextBoldCursor(context);
        appIdField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        appIdField.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        appIdField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        appIdField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        appIdField.setCursorSize(AndroidUtilities.dp(20));
        appIdField.setCursorWidth(1.5f);
        appIdField.setSingleLine(true);
        appIdField.setInputType(InputType.TYPE_CLASS_NUMBER);
        appIdField.setHint(LocaleController.isRTL ? "לדוגמה: 12345678" : "e.g. 12345678");
        appIdField.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_text_RedRegular));
        if (BuildVars.APP_ID > 0) {
            appIdField.setText(String.valueOf(BuildVars.APP_ID));
        }
        formCard.addView(appIdField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 46, 0, 0, 0, 16));

        // Field 2: App api_hash
        LinearLayout hashHeaderLayout = new LinearLayout(context);
        hashHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
        formCard.addView(hashHeaderLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        TextView appHashLabel = new TextView(context);
        appHashLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        appHashLabel.setTypeface(AndroidUtilities.bold());
        appHashLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        appHashLabel.setText(LocaleController.isRTL ? "App api_hash (32 תווים)" : "App api_hash (32 hex characters)");
        hashHeaderLayout.addView(appHashLabel, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        TextView pasteButton = new TextView(context);
        pasteButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        pasteButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        pasteButton.setText(LocaleController.isRTL ? "הדבק" : "Paste");
        pasteButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(2), AndroidUtilities.dp(8), AndroidUtilities.dp(2));
        pasteButton.setOnClickListener(v -> {
            try {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null && clipboard.hasPrimaryClip() && clipboard.getPrimaryClip() != null) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    if (item != null && item.getText() != null) {
                        String pasted = item.getText().toString().trim();
                        appHashField.setText(pasted);
                        appHashField.setSelection(pasted.length());
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        });
        hashHeaderLayout.addView(pasteButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        appHashField = new EditTextBoldCursor(context);
        appHashField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        appHashField.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        appHashField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        appHashField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        appHashField.setCursorSize(AndroidUtilities.dp(20));
        appHashField.setCursorWidth(1.5f);
        appHashField.setSingleLine(true);
        appHashField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        appHashField.setHint(LocaleController.isRTL ? "מחרוזת של 32 אותיות ומספרים" : "32 hex characters");
        appHashField.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_text_RedRegular));
        if (!TextUtils.isEmpty(BuildVars.APP_HASH)) {
            appHashField.setText(BuildVars.APP_HASH);
        }
        formCard.addView(appHashField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 46));

        // Save & Continue Button
        TextView saveButton = new TextView(context);
        saveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        saveButton.setTypeface(AndroidUtilities.bold());
        saveButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        saveButton.setGravity(Gravity.CENTER);
        saveButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(10), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground)));
        saveButton.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(14), AndroidUtilities.dp(16), AndroidUtilities.dp(14));
        saveButton.setText(isFromSettings ?
                (LocaleController.isRTL ? "שמור שינויים" : "Save Changes") :
                (LocaleController.isRTL ? "שמור והמשך" : "Save and Continue"));
        saveButton.setOnClickListener(v -> saveAndContinue());
        contentLayout.addView(saveButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 16));

        fragmentView = contentView;
        return fragmentView;
    }

    private void saveAndContinue() {
        if (getParentActivity() == null) {
            return;
        }

        String appIdStr = appIdField != null && appIdField.getText() != null ? appIdField.getText().toString().trim() : "";
        String appHashStr = appHashField != null && appHashField.getText() != null ? appHashField.getText().toString().trim() : "";

        // Validate App ID
        if (TextUtils.isEmpty(appIdStr)) {
            showErrorAlert(LocaleController.isRTL ? "נא להזין App api_id" : "Please enter App api_id");
            return;
        }

        int appId;
        try {
            appId = Integer.parseInt(appIdStr);
            if (appId <= 0) {
                showErrorAlert(LocaleController.isRTL ? "ה-App api_id חייב להיות מספר חיובי" : "App api_id must be a positive number");
                return;
            }
        } catch (Exception e) {
            showErrorAlert(LocaleController.isRTL ? "ה-App api_id שהוזן אינו מספר תקין" : "App api_id is not a valid number");
            return;
        }

        // Validate App Hash
        if (TextUtils.isEmpty(appHashStr)) {
            showErrorAlert(LocaleController.isRTL ? "נא להזין App api_hash" : "Please enter App api_hash");
            return;
        }

        if (appHashStr.length() != 32) {
            showErrorAlert(LocaleController.isRTL ?
                    "ה-App api_hash חייב להכיל בדיוק 32 תווים (הוזנו " + appHashStr.length() + " תווים)" :
                    "App api_hash must be exactly 32 characters (entered " + appHashStr.length() + " characters)");
            return;
        }

        for (int i = 0; i < appHashStr.length(); i++) {
            char c = appHashStr.charAt(i);
            boolean isHex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!isHex) {
                showErrorAlert(LocaleController.isRTL ? "ה-App api_hash חייב להכיל רק תווים הקסדצימליים (0-9, a-f)" : "App api_hash must contain only hexadecimal characters (0-9, a-f)");
                return;
            }
        }

        // Persist API Keys
        SharedConfig.saveApiKeys(appId, appHashStr);

        // Notify ConnectionsManager across all accounts
        ConnectionsManager.onApiKeysUpdated();

        if (isFromSettings) {
            Toast.makeText(getParentActivity(), LocaleController.isRTL ? "מפתחות ה-API עודכנו בהצלחה" : "API keys updated successfully", Toast.LENGTH_SHORT).show();
            finishFragment();
        } else {
            BaseFragment nextFragment;
            if (LoginActivity.loadCurrentState(false, currentAccount).getInt("currentViewNum", 0) != 0) {
                nextFragment = new LoginActivity();
            } else {
                nextFragment = new IntroActivity();
            }
            presentFragment(nextFragment, true);
        }
    }

    private void showErrorAlert(String message) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.isRTL ? "שגיאה בפרטים" : "Invalid Details");
        builder.setMessage(message);
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    @Override
    public boolean onBackPressed(boolean invoked) {
        if (!isFromSettings && !BuildVars.hasValidApiKeys()) {
            // Cannot dismiss if initial keys are not configured
            return false;
        }
        return super.onBackPressed(invoked);
    }
}
