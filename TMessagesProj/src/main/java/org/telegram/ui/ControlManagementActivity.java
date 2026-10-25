package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class ControlManagementActivity extends BaseFragment {

    private UniversalRecyclerView listView;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.ControlAndManagement));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout contentView = new FrameLayout(context);
        contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new UniversalRecyclerView(this, this::fillItems, this::onClick, this::onLongClick);
        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        fragmentView = contentView;
        return fragmentView;
    }

    private void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("ניהול משתמשים וערוצים"));
        
        UItem profilePhotos = UItem.asSwitch(1, "השבתת תמונות פרופיל").setChecked(SharedConfig.disable_profile_photos);
        items.add(profilePhotos);
        items.add(UItem.asShadow("כאשר מופעל, תמונות פרופיל לא יוצגו"));

        UItem channels = UItem.asSwitch(2, "חסימת ערוצים").setChecked(SharedConfig.disable_channels);
        items.add(channels);
        items.add(UItem.asShadow("כאשר מופעל, לא תתאפשר כניסה לערוצים"));

        if (SharedConfig.disable_channels) {
            int count = SharedConfig.allowed_channel_ids.size();
            UItem allowedChannels = UItem.asButton(8, "ערוצים מותרים", count + "/3");
            items.add(allowedChannels);
            items.add(UItem.asShadow("בחר עד 3 ערוצים מאלו שאתה עוקב אחריהם שיהיו פתוחים גם כשערוצים חסומים"));
        }

        UItem groups = UItem.asSwitch(3, "חסימת קבוצות").setChecked(SharedConfig.disable_groups);
        items.add(groups);
        items.add(UItem.asShadow("כאשר מופעל, לא תתאפשר כניסה לקבוצות וקבוצות-על"));

        UItem sensitiveContent = UItem.asSwitch(4, "אל תאפשר הצגת תוכן פוגעני").setChecked(SharedConfig.disable_sensitive_content);
        items.add(sensitiveContent);
        items.add(UItem.asShadow("חוסם תוכן המוגדר מבחינת Telegram כתוכן לגילאי 18+. לרוב, כולל אלימות ותכנים בוטים"));

        UItem privacySettings = UItem.asSwitch(5, "אל תאפשר שינוי הגדרות פרטיות").setChecked(SharedConfig.disable_privacy_settings);
        items.add(privacySettings);
        items.add(UItem.asShadow("כאשר מופעל, המשתמש לא יוכל לשנות הגדרות פרטיות"));

        UItem addDevices = UItem.asSwitch(6, "חסימת הוספת מכשירים").setChecked(SharedConfig.disable_add_devices);
        items.add(addDevices);
        items.add(UItem.asShadow("כאשר מופעל, לא תתאפשר כניסה להגדרות מכשירים והתראות מערכת מטלגרם יוסתרו"));

        UItem bots = UItem.asSwitch(7, "חסימת בוטים ומיני-אפליקציות").setChecked(SharedConfig.disable_bots);
        items.add(bots);
        items.add(UItem.asShadow("כאשר מופעל, תחסם הגישה לבוטים, אפליקציות רשת ומשחקים"));

        items.add(UItem.asHeader("הגדרות API"));
        String keyStatus = BuildVars.hasValidApiKeys() ? ("ID: " + BuildVars.APP_ID) : "לא הוגדר";
        items.add(UItem.asButton(9, "מפתחות Telegram API", keyStatus));
        items.add(UItem.asShadow("צפייה ועריכה של מפתחות ה-API שבאמצעותם האפליקציה מתחברת לשרתי טלגרם"));
    }

    private void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == 1) {
            SharedConfig.disable_profile_photos = !SharedConfig.disable_profile_photos;
            item.checked = SharedConfig.disable_profile_photos;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 2) {
            SharedConfig.disable_channels = !SharedConfig.disable_channels;
            item.checked = SharedConfig.disable_channels;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 3) {
            SharedConfig.disable_groups = !SharedConfig.disable_groups;
            item.checked = SharedConfig.disable_groups;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 4) {
            SharedConfig.disable_sensitive_content = !SharedConfig.disable_sensitive_content;
            item.checked = SharedConfig.disable_sensitive_content;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 5) {
            SharedConfig.disable_privacy_settings = !SharedConfig.disable_privacy_settings;
            item.checked = SharedConfig.disable_privacy_settings;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 6) {
            SharedConfig.disable_add_devices = !SharedConfig.disable_add_devices;
            item.checked = SharedConfig.disable_add_devices;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 7) {
            SharedConfig.disable_bots = !SharedConfig.disable_bots;
            item.checked = SharedConfig.disable_bots;
            listView.adapter.update(true);
            SharedConfig.saveConfig();
        } else if (item.id == 8) {
            showAllowedChannelsDialog();
        } else if (item.id == 9) {
            presentFragment(new ApiKeysSetupActivity(true));
        }
    }

    private void showAllowedChannelsDialog() {
        if (getParentActivity() == null) {
            return;
        }

        // Collect all subscribed channels
        MessagesController messagesController = MessagesController.getInstance(currentAccount);
        ArrayList<TLRPC.Dialog> allDialogs = messagesController.getAllDialogs();
        
        ArrayList<TLRPC.Chat> subscribedChannels = new ArrayList<>();
        for (TLRPC.Dialog dialog : allDialogs) {
            if (dialog.id < 0) {
                TLRPC.Chat chat = messagesController.getChat(-dialog.id);
                if (chat != null && ChatObject.isChannelAndNotMegaGroup(chat)) {
                    subscribedChannels.add(chat);
                }
            }
        }

        if (subscribedChannels.isEmpty()) {
            Toast.makeText(getParentActivity(), "לא נמצאו ערוצים שאתה עוקב אחריהם", Toast.LENGTH_SHORT).show();
            return;
        }

        HashSet<Long> tempSelected = new HashSet<>(SharedConfig.allowed_channel_ids);

        // Build custom view with CheckBoxCells
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<CheckBoxCell> checkBoxCells = new ArrayList<>();

        for (int i = 0; i < subscribedChannels.size(); i++) {
            TLRPC.Chat chat = subscribedChannels.get(i);
            CheckBoxCell cell = new CheckBoxCell(getParentActivity(), CheckBoxCell.TYPE_CHECK_BOX_DEFAULT);
            cell.setText(chat.title, "", tempSelected.contains(chat.id), i < subscribedChannels.size() - 1);
            cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
            cell.setTag(chat.id);
            checkBoxCells.add(cell);

            cell.setOnClickListener(v -> {
                long chatId = (long) v.getTag();
                CheckBoxCell clickedCell = (CheckBoxCell) v;
                boolean currentlyChecked = clickedCell.isChecked();

                if (!currentlyChecked) {
                    // Trying to check — enforce limit
                    if (tempSelected.size() >= 3) {
                        Toast.makeText(getParentActivity(), "ניתן לבחור עד 3 ערוצים בלבד", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tempSelected.add(chatId);
                    clickedCell.setChecked(true, true);
                } else {
                    tempSelected.remove(chatId);
                    clickedCell.setChecked(false, true);
                }
            });

            linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
        }

        ScrollView scrollView = new ScrollView(getParentActivity());
        scrollView.addView(linearLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("בחר ערוצים מותרים (עד 3)");
        builder.setView(scrollView);
        builder.setPositiveButton("שמור", (dialog, which) -> {
            SharedConfig.allowed_channel_ids = new HashSet<>(tempSelected);
            SharedConfig.saveConfig();
            listView.adapter.update(true);
        });
        builder.setNegativeButton("ביטול", null);
        builder.show();
    }

    private boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listView != null && listView.adapter != null) {
            listView.adapter.update(true);
        }
    }
}
