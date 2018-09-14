package onewalletui.util;

/*
 * Copyright 2011-2014 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import android.content.Context;

import org.CoreUtils;
import org.spongycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import onemessageui.dialog.DialogUtil;
import onewalletui.util.jump.JumpAppPageUtil;

/**
 * @author Andreas Schildbach
 * @author John L. Jegutanis
 */
public class WalletUtils {

    public static int ENTROPY_SIZE_DEBUG = -1;

    public static String localeCurrencyCode() {
        try {
            return Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        } catch (final IllegalArgumentException x) {
            return null;
        }
    }

    public static boolean checkSaveSeed(final Context context) {
        boolean ifHaveSeed = true;
        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED)) {
            try {
                Object o = SharePreferenceUtils.getObject(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED);
                if (o != null) {
                    ifHaveSeed = (boolean) o;
                }
            } catch (Exception e) {

            }
        }
        if (!ifHaveSeed) {
            DialogUtil.simpleDialog(context, context.getString(R.string.please_verify_your_seed), context.getString(R.string.now_verify),
                    context.getString(R.string.button_cancel), new DialogUtil.ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            DialogUtil.checkPswDialog(context, context.getString(R.string.enter_password), new DialogUtil.ConfirmCallBackInf() {
                                @Override
                                public void onConfirmClick(String content) {
                                    String mySeed = OneAccountHelper.getDefaultAccount().brain_key;
                                    JumpAppPageUtil.jumpCreateSeedPage(context, mySeed, false);
                                }
                            });
                        }
                    });
        }
        return ifHaveSeed;
    }

    public static String generateRandomId() {
        return generateRandomId(32);
    }

    public static String generateRandomId(int length) {
        byte[] randomIdBytes = new byte[length];
        new SecureRandom().nextBytes(randomIdBytes);
        return Hex.toHexString(randomIdBytes);
    }

    public static String generateMnemonicString(int entropyBitsSize) {
        return mnemonicToString(generateMnemonic(entropyBitsSize));
    }

    public static List<String> generateMnemonic(int entropyBitsSize) {
        byte[] entropy;
        if (ENTROPY_SIZE_DEBUG > 0) {
            entropy = new byte[ENTROPY_SIZE_DEBUG];
        } else {
            entropy = new byte[(entropyBitsSize / 8)];
        }
        new SecureRandom().nextBytes(entropy);
        return CoreUtils.bytesToMnemonic(entropy);
    }

    public static String mnemonicToString(List<String> mnemonicWords) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mnemonicWords.size(); i++) {
            if (i != 0) {
                sb.append(' ');
            }
            sb.append((String) mnemonicWords.get(i));
        }
        return sb.toString();
    }
}
