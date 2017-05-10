package piuk.blockchain.android.util;

import android.util.Log;
import android.util.Pair;

import info.blockchain.wallet.payload.PayloadManager;
import piuk.blockchain.android.injection.Injector;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class BackupWalletUtil {

    @Inject protected PrefsUtil prefs;
    @Inject protected PayloadManager payloadManager;

    public BackupWalletUtil() {
        Injector.getInstance().getAppComponent().inject(this);
    }

    /**
     * Return ordered list of integer, string pairs which can be used to confirm mnemonic.
     *
     * @return List<Pair<Integer,String>>
     */
    public List<Pair<Integer, String>> getConfirmSequence(String secondPassword) {

        List<Pair<Integer, String>> toBeConfirmed = new ArrayList<>();
        List<String> s = getMnemonic(secondPassword);
        SecureRandom random = new SecureRandom();
        List<Integer> seen = new ArrayList<>();

        int sel;
        int i = 0;
        while (i < 3) {
            sel = random.nextInt(s.size());
            if (!seen.contains(sel)) {
                seen.add(sel);
                i++;
            }
        }

        Collections.sort(seen);

        for (int ii = 0; ii < 3; ii++) {
            toBeConfirmed.add(new Pair<>(seen.get(ii), s.get(seen.get(ii))));
        }

        return toBeConfirmed;
    }

    /**
     * Return mnemonic in the form of a string array.
     *
     * @return String[]
     */
    public List<String> getMnemonic(String secondPassword) {

        // TODO: 10/05/2017 Get seed from secure place
        String seedHex = prefs.getValue("seedHex", null);

        try {
            payloadManager.getPayload().decryptHDWallet(0, secondPassword);
            return payloadManager.getPayload().getHdWallets().get(0).getMnemonic(seedHex);
        } catch (Exception e) {
            Log.e(BackupWalletUtil.class.getSimpleName(), "getMnemonic: ", e);
            return null;
        }
    }
}
