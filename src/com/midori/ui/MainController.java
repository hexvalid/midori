package com.midori.ui;

import com.midori.bot.*;
import com.midori.database.DBAccTools;
import com.midori.database.DBCon;
import com.midori.database.DBSetTools;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

    @FXML
    private TableView<Account> _atc;

    @FXML
    private TableColumn<Account, Integer> _atc_id;

    @FXML
    private HBox _aa_pane;

    @FXML
    private TableColumn<Account, String> _atc_email;

    @FXML
    private TableColumn<Account, Double> _atc_balance;

    @FXML
    private TableColumn<Account, Integer> _atc_rewardpoints;

    @FXML
    private TableColumn<Account, Integer> _atc_refferer;

    @FXML
    private TableColumn<Account, Integer> _atc_fpplayed;

    @FXML
    private TableColumn<Account, Double> _atc_fpbonusreqcompleted;

    @FXML
    private TableColumn<Account, String> _atc_fpstatus;

    @FXML
    private TableColumn<Account, String> _atc_boosts;

    @FXML
    private TableColumn<Account, String> _atc_proxy;

    @FXML
    private TableColumn<Account, String> _atc_status;

    @FXML
    private Label _acm_id;

    @FXML
    private CheckBox _acm_disablelottery;

    @FXML
    private HBox _acm_email_verification_box;

    @FXML
    private Button _acm_email_verification_send_button;

    @FXML
    private TextField _acm_email_verification;

    @FXML
    private Button _acm_email_verification_verificate_button;

    @FXML
    private ComboBox<Boost> _acm_boosts;

    @FXML
    private TextField _aa_email;

    @FXML
    private PasswordField _aa_password;

    @FXML
    private TextField _aa_tfa_secret;

    @FXML
    private TextField _aa_referrer;

    @FXML
    private TextField _aa_useragent;

    @FXML
    private Button _aa_useragent_gen;

    @FXML
    private TextField _aa_language;

    @FXML
    private Button _aa_language_gen;

    @FXML
    private ComboBox<String> _aa_timeoffset;

    @FXML
    private ComboBox<String> _aa_proxy;

    @FXML
    private TextField _aa_proxy_address;

    @FXML
    private Button _aa_checkproxy;

    @FXML
    private Button _aa_addaccount;

    @FXML
    private CheckBox _aa_exitingaccount;

    @FXML
    private TextField _aa_email_auth;

    @FXML
    private Button _aa_email_auth_button;

    @FXML
    private TextField _set_proxyusername;

    @FXML
    private PasswordField _set_proxypassword;

    @FXML
    private TextField _set_anticaptchakey;

    @FXML
    private TextField _set_aiserver;

    @FXML
    private Button _set_updateproxycredentials;

    @FXML
    private Button _set_savesettings;

    @FXML
    public ListView<TextFlow> _log;

    @FXML
    public Button _acm_home;

    @FXML
    public Button _acc_roll;

    @FXML
    private Button _acm_enable_tfa;

    @FXML
    private Label _acm_tfa_status;

    @FXML
    private Button _acm_v3;

    @FXML
    private Button _acm_playmartingale, _acm_simulatemartingale;

    @FXML
    private TextField _acm_targetprofit, _acm_martingalesleep;

    @FXML
    private ProgressBar _acm_martingalebar, _acm_martingaleprofitbar;

    @FXML
    private Label _acm_martingaleprofit, _acm_simulatemartingaleanswer, _acm_martingaletierinfo;

    @FXML
    private CheckBox _acm_randomizemartingale;

    //Other vars
    static List<Account> accounts;
    private String tempProxyAddress;

    private Account aa_account;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            DBCon.Connect();
            accounts = DBAccTools.GetAllAccounts();
            DBSetTools.GetSettingsFromDB();
            Tools.UpdateProxyCredentials(DBSetTools.SET_PROXY_USERNAME, DBSetTools.SET_PROXY_PASSWORD);

            _atc_id.setCellValueFactory(d -> d.getValue().idProperty().asObject());
            _atc_email.setCellValueFactory(d -> d.getValue().emailProperty());
            _atc_balance.setCellValueFactory(d -> d.getValue().balanceProperty().asObject());
            _atc_balance.setCellFactory(tc -> doubleSetter("%.8f"));
            _atc_rewardpoints.setCellValueFactory(d -> d.getValue().rewardPointsProperty().asObject());
            _atc_refferer.setCellValueFactory(d -> d.getValue().referrerProperty().asObject());
            _atc_fpplayed.setCellValueFactory(d -> d.getValue().fpPlayedProperty().asObject());
            _atc_fpbonusreqcompleted.setCellValueFactory(d -> d.getValue().fpBonusReqCompletedProperty().asObject());
            _atc_fpbonusreqcompleted.setCellFactory(tc -> doubleSetter("%.4f"));
            _atc_fpstatus.setCellValueFactory(d -> d.getValue()._fpStatusProperty());
            _atc_boosts.setCellValueFactory(d -> d.getValue().boostsProperty());
            _atc_proxy.setCellValueFactory(d -> d.getValue().proxyProperty());
            _atc_status.setCellValueFactory(d -> d.getValue()._statusProperty());

            _atc.getItems().setAll(accounts);


            _atc.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    _acm_id.setText(newS.idProperty().asString().get());
                    _acm_disablelottery.setSelected(newS.disableLottery);
                    if (!_aa_exitingaccount.isSelected()) {
                        _aa_referrer.setText(String.valueOf(newS.idProperty().get()));
                    }
                    if (newS.tfaEnabled) {
                        _acm_tfa_status.setText("TFA Enabled");
                    } else {
                        _acm_tfa_status.setText("TFA Disabled");
                    }
                    _acm_enable_tfa.setDisable(newS.tfaEnabled);
                    _acm_email_verification_box.setVisible(!newS.emailConfirmed);

                }
            });


            _aa_useragent.setText(Rune.defaultUserAgent);
            _aa_language.setText(Rune.defaultAcceptLanguage);
            _aa_proxy.getItems().setAll("ON", "OFF", "DEBUG");
            _aa_proxy.getSelectionModel().select(0);
            _aa_timeoffset.getItems().setAll(Rune.timeZoneOffsetMap.keySet());
            _aa_timeoffset.getSelectionModel().select(41);


            _acm_boosts.getItems().setAll(Boost.BOOSTS);
            _acm_boosts.getSelectionModel().select(0);
            _acm_boosts.setConverter(new StringConverter<Boost>() {
                @Override
                public String toString(Boost obj) {
                    return obj.name + " (" + obj.cost + ")";
                }

                @Override
                public Boost fromString(String s) {
                    return null;
                }
            });

            _set_proxyusername.setText(DBSetTools.SET_PROXY_USERNAME);
            _set_proxypassword.setText(DBSetTools.SET_PROXY_PASSWORD);
            _set_anticaptchakey.setText(DBSetTools.SET_ANTICAPTCHA_KEY);
            _set_aiserver.setText(DBSetTools.SET_AI_SERVER);


            Log.Print(Log.t.INF, Prefs.VERSION);

            Log.Print(Log.t.WRN, "This is a testing version for development & feedback purposes");

            Log.Print(Log.t.SCS, "UI initiliazed.");


            ExecutorService exService = Executors.newSingleThreadExecutor();
            exService.submit(new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    while (true) {
                        Date now = new Date();
                        for (Account a : accounts) {
                            if (a.isReadyForRoll()) {
                                a.set_FPStatus("Ready");
                            } else {
                                long diffInMillies = Math.abs(now.getTime() - DateUtils.addHours(a.lastFPDate, 1).getTime());
                                int diffSeconds = (int) (diffInMillies / 1000 % 60);
                                int diffMinutes = (int) (diffInMillies / (60 * 1000) % 60);
                                a.set_FPStatus(String.format("%02d", diffMinutes) + " minutes " + String.format("%02d", diffSeconds) + " seconds");
                            }
                        }


                        Thread.sleep(1000);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @FXML
    void _aa_addAccount() {
        new Thread(() -> {
            boolean checkProxy;
            String proxy, proxyAddress = null;
            if (aa_account == null) {
                Platform.runLater(() -> {
                    _aa_pane.setDisable(true);
                    _aa_checkproxy.setDisable(true);
                    _aa_addaccount.setDisable(true);
                    _aa_exitingaccount.setDisable(true);
                });
                proxy = _aa_proxy.getSelectionModel().getSelectedItem();
                proxyAddress = "OFF";
                if (proxy.equals("ON")) {
                    checkProxy = Tools.CheckProxy(_aa_proxy_address.getText());
                    if (checkProxy) {
                        proxyAddress = _aa_proxy_address.getText();
                    }
                } else {
                    checkProxy = true;
                    proxyAddress = _aa_proxy.getSelectionModel().getSelectedItem();
                }
            } else {
                checkProxy = true;
            }

            if (checkProxy) {
                if (aa_account == null) {
                    aa_account = new Account(
                            _aa_email.getText(),
                            _aa_password.getText(),
                            Integer.parseInt(_aa_referrer.getText()),
                            _aa_tfa_secret.getLength() > 3,
                            _aa_tfa_secret.getText(),
                            Rune.timeZoneOffsetSolver(_aa_timeoffset.getSelectionModel().getSelectedItem()),
                            _aa_useragent.getText(),
                            _aa_language.getText(),
                            proxyAddress);
                } else {
                    Log.Print(Log.t.INF, "Bypassing email auth...");
                    try {
                        Engine.Open(aa_account, _aa_email_auth.getText().replaceAll("email_verify", "email_verify_conf"));
                    } catch (IOException e) {
                        Log.Print(Log.t.ERR, e.getMessage());
                    }
                }
                try {
                    Log.Print(Log.t.INF, "Adding account...");
                    Engine.Login(aa_account, !_aa_exitingaccount.isSelected());
                    Log.Print(Log.t.INF, "Homing account...");
                    Engine.Home(aa_account, null, Engine.URL.root);
                    Log.Print(Log.t.INF, "Account adding to database...");
                    DBAccTools.InsertAccount(aa_account);
                    accounts.add(aa_account);
                    _atc.getItems().add(aa_account);
                    _atc.refresh();
                    Log.Print(Log.t.SCS, "Account added");
                    aa_account = null;
                    Platform.runLater(() -> {
                        _aa_pane.setDisable(false);
                        _aa_checkproxy.setDisable(false);
                        _aa_addaccount.setDisable(false);
                        _aa_exitingaccount.setDisable(false);
                        _aa_email_auth.setVisible(false);
                        _aa_email_auth_button.setVisible(false);
                    });
                } catch (Exception e) {
                    if (e.getMessage().contains("email inbox for a link to auth")) {
                        Log.Print(Log.t.WRN, "Enter the email auth link");
                        Platform.runLater(() -> {
                            _aa_email_auth.setVisible(true);
                            _aa_email_auth_button.setVisible(true);
                            _aa_addaccount.setDisable(false);
                            _aa_email_auth.requestFocus();
                        });
                    } else {
                        Log.Print(Log.t.ERR, "Account cant added: " + e.getMessage());
                        aa_account = null;
                        Platform.runLater(() -> {
                            _aa_pane.setDisable(false);
                            _aa_checkproxy.setDisable(false);
                            _aa_addaccount.setDisable(false);
                            _aa_exitingaccount.setDisable(false);
                        });
                    }
                }

            } else {
                _aa_pane.setDisable(false);
                _aa_checkproxy.setDisable(false);
                _aa_addaccount.setDisable(false);
                _aa_exitingaccount.setDisable(false);
            }


        }).start();
    }

    @FXML
    void _aa_cancel() {
        aa_account = null;
        Platform.runLater(() -> {
            _aa_pane.setDisable(false);
            _aa_checkproxy.setDisable(false);
            _aa_addaccount.setDisable(false);
            _aa_exitingaccount.setDisable(false);
            _aa_email_auth.setText("");
            _aa_email_auth.setVisible(false);
            _aa_email_auth_button.setVisible(false);
        });
    }

    @FXML
    void _aa_checkProxy() {
        new Thread(() -> {
            Platform.runLater(() -> _aa_checkproxy.setDisable(true));
            Tools.CheckProxy(_aa_proxy_address.getText());
            Platform.runLater(() -> _aa_checkproxy.setDisable(false));
        }).start();
    }

    @FXML
    void _set_updateProxyCredentials() {
        Tools.UpdateProxyCredentials(_set_proxyusername.getText(), _set_proxypassword.getText());
        Log.Print(Log.t.INF, "Proxy Credentials updated");
    }

    @FXML
    void _set_saveSettings() {
        try {
            DBSetTools.SET_PROXY_USERNAME = _set_proxyusername.getText();
            DBSetTools.SET_PROXY_PASSWORD = _set_proxypassword.getText();
            DBSetTools.SET_ANTICAPTCHA_KEY = _set_anticaptchakey.getText();
            DBSetTools.SET_AI_SERVER = _set_aiserver.getText();
            DBSetTools.SaveSettingsToDB();
            Log.Print(Log.t.SCS, "Settings saved to database");
        } catch (SQLException e) {
            Log.Print(Log.t.ERR, "Settings cannot saved to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void _acmStart() {
        _atc.getSelectionModel().getSelectedItem().startExecuter();
    }

    @FXML
    void _acmStop() {
        _atc.getSelectionModel().getSelectedItem().stopExecuter();
    }

    @FXML
    void _acmHome() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_home.setDisable(true));
                Engine.Home(_atc.getSelectionModel().getSelectedItem(), null, Engine.URL.home);
                DBAccTools.UpdateAccountForRoll(_atc.getSelectionModel().getSelectedItem());
            } catch (IOException | SQLException | InterruptedException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_home.setDisable(false));
            }

        }).start();
    }

    @FXML
    void _aa_changeProxy() {
        if (_aa_proxy.getValue().equals("ON")) {
            _aa_proxy_address.setDisable(false);
            _aa_checkproxy.setDisable(false);
            _aa_proxy_address.setText(tempProxyAddress);
        } else if (_aa_proxy.getValue().equals("DEBUG")) {
            if (_aa_proxy_address.getText().length() > 1 && !_aa_proxy_address.getText().equals("127.0.0.1:8080")) {
                tempProxyAddress = _aa_proxy_address.getText();
            }
            _aa_proxy_address.setText("127.0.0.1:8080");
            _aa_proxy_address.setDisable(true);
            _aa_checkproxy.setDisable(true);
        } else {
            if (_aa_proxy_address.getText().length() > 1 && !_aa_proxy_address.getText().equals("127.0.0.1:8080")) {
                tempProxyAddress = _aa_proxy_address.getText();
            }
            _aa_proxy_address.setText("");
            _aa_proxy_address.setDisable(true);
            _aa_checkproxy.setDisable(true);
        }
    }


    @FXML
    void _aa_changeExitingAccount() {
        if (_aa_exitingaccount.isSelected()) {
            _aa_tfa_secret.setDisable(false);
            _aa_referrer.setText("0");
            _aa_referrer.setDisable(true);
        } else {
            _aa_tfa_secret.setDisable(true);
            _aa_referrer.setDisable(false);
        }
    }

    @FXML
    void _aa_generateUserAgent() {
        _aa_useragent.setText(Rune.getRandomUserAgent());
    }

    @FXML
    void _aa_generateAcceptLanguage() {
        _aa_language.setText(Rune.getRandomAcceptLanguage());
    }

    @FXML
    void _acm_disableLottery() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_disablelottery.setDisable(true));
                Engine.toggleLottery(_atc.getSelectionModel().getSelectedItem());
                _acm_disablelottery.setSelected(_atc.getSelectionModel().getSelectedItem().disableLottery);
                DBAccTools.UpdateAccountForRoll(_atc.getSelectionModel().getSelectedItem());
            } catch (IOException | URISyntaxException | SQLException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_disablelottery.setDisable(false));
            }
        }).start();
    }

    @FXML
    void _acm_sendLink() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_email_verification_send_button.setDisable(true));
                Engine.confirmEmail(_atc.getSelectionModel().getSelectedItem());
            } catch (IOException | URISyntaxException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_email_verification_send_button.setDisable(false));
            }
        }).start();
    }

    @FXML
    void _acm_verificate() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_email_verification_verificate_button.setDisable(true));
                Log.Print(Log.t.INF, "Confirming email verification link...");
                Engine.Open(_atc.getSelectionModel().getSelectedItem(),
                        _acm_email_verification.getText().replaceAll("email_verify", "email_verify_conf"));
                Thread.sleep(2000);
                Engine.Home(_atc.getSelectionModel().getSelectedItem(), "?op=home&tab=edit&tab2=enable_2fa",
                        _acm_email_verification.getText().replaceAll("email_verify", "email_verify_conf"));
                DBAccTools.UpdateAccountForRoll(_atc.getSelectionModel().getSelectedItem());
                _acm_email_verification_box.setVisible(!_atc.getSelectionModel().getSelectedItem().emailConfirmed);
            } catch (IOException | SQLException | InterruptedException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_email_verification_verificate_button.setDisable(false));
            }
        }).start();
    }

    @FXML
    void _acm_enableTfa() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_enable_tfa.setDisable(true));
                Engine.enableTFA(_atc.getSelectionModel().getSelectedItem());
            } catch (IOException | URISyntaxException | SQLException | GeneralSecurityException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_enable_tfa.setDisable(false));
            }
        }).start();
    }


    @FXML
    void _sendV3() {
        new Thread(() -> {
            try {
                _atc.getSelectionModel().getSelectedItem().openHTTPClient();

                Platform.runLater(() -> _acm_v3.setDisable(true));
                Engine.RecordRecaptchaV3(_atc.getSelectionModel().getSelectedItem());
            } catch (IOException | URISyntaxException | InterruptedException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_v3.setDisable(false));
                try {
                    _atc.getSelectionModel().getSelectedItem().closeHTTPClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public TableCell doubleSetter(String format) {
        return new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format(format, value));
                }
            }
        };
    }


    public void _dShuffle() throws SQLException {
        Log.Print(Log.t.DBG, "Shuffle start...");
        for (Account a : accounts) {
            if (a.isReadyForRoll()) {
                a.lastFPDate = new java.sql.Date(DateUtils.addSeconds(new Date(), -RandomUtils.nextInt(1, 3600)).getTime());
                DBAccTools.UpdateLastFPDate(a);
            }
            a.lastV3Date = new java.sql.Date(DateUtils.addMinutes(new Date(), -RandomUtils.nextInt(1, Prefs.RECAPTCHA_V3_TIME_MINUTES)).getTime());
            DBAccTools.UpdateAccountForRoll(a);
        }
        Log.Print(Log.t.DBG, "Shuffle end.");


    }

    public void _dStartAll() {
        Log.Print(Log.t.DBG, "Start all start...");
        for (Account a : accounts) {
            a.startExecuter();
        }
        Log.Print(Log.t.DBG, "Start all end.");
    }

    public void _dStopAll() {
        Log.Print(Log.t.DBG, "Stop all start...");
        for (Account a : accounts) {
            a.stopExecuter();
        }
        Log.Print(Log.t.DBG, "Stop all end.");
    }

    public void _acmRedeem() throws IOException, URISyntaxException, Engine.BoostError {
        Log.Print(Log.t.DBG, "Redeeming...");
        Engine.RedeemRewards(_atc.getSelectionModel().getSelectedItem(), _acm_boosts.getSelectionModel().getSelectedItem());
    }


    @FXML
    void _playMartingale() {
        Account a = _atc.getSelectionModel().getSelectedItem();
        new Thread(() -> {
            try {
                a.openHTTPClient();
                Platform.runLater(() -> _acm_playmartingale.setDisable(true));


                double startBalance = a.getBalance();

                double profit = Double.parseDouble(_acm_targetprofit.getText());
                double targetAmount = 0.00000001;

                double amount = targetAmount;

                boolean lastStat = true;

                int targetSleep = Integer.parseInt(_acm_martingalesleep.getText());
                int sleep = targetSleep;
                boolean randomize = _acm_randomizemartingale.isSelected();
                while (true) {

                    if (a.getBalance() >= startBalance + profit) {
                        Log.Print(Log.t.INF, a.logDomain() + "Betting: WIN END");
                        break;
                    }

                    if (amount > a.getBalance()) {
                        Log.Print(Log.t.INF, a.logDomain() + "Betting: LOST END");
                        break;
                    }
                    lastStat = Engine.Bet(_atc.getSelectionModel().getSelectedItem(), amount, randomize);

                    if (!lastStat) {
                        sleep = sleep * 2;
                        amount = amount * 2;
                    } else {
                        amount = targetAmount;
                        sleep = targetSleep;
                    }
                    Platform.runLater(() -> {
                        _acm_martingaleprofit.setText(String.format("%.8f", a.getBalance() - startBalance));
                        _acm_martingalebar.setProgress(a.getBalance() / (startBalance + profit));
                        _acm_martingaleprofitbar.setProgress(Math.max(0, (a.getBalance() - startBalance) / profit));
                    });

                    Thread.sleep(sleep);
                }
            } catch (IOException | URISyntaxException | Engine.BetError | InterruptedException e) {
                Log.Print(Log.t.ERR, e.getMessage());
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> _acm_playmartingale.setDisable(false));
                try {
                    _atc.getSelectionModel().getSelectedItem().closeHTTPClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    DBAccTools.UpdateAccountForRoll(a);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    void _simulateMartingale() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> _acm_simulatemartingale.setDisable(true));
                double balance = _atc.getSelectionModel().getSelectedItem().getBalance();
                double profit = Double.parseDouble(_acm_targetprofit.getText());
                int win = 0;
                for (int i = 0; i < 10000; i++) {
                    if (sim(balance, profit)) {
                        win++;
                    }
                }
                double stat = ((win / 10000.0) * 100);

                Platform.runLater(() -> _acm_simulatemartingaleanswer.setText(String.format("%.2f", stat) + "%"));
                double target = _atc.getSelectionModel().getSelectedItem().getBalance();
                for (int i = 0; i < martingaleTiers.length; i++) {
                    if (target >= martingaleTiers[i] / 100000000.0 && target < martingaleTiers[i + 1] / 100000000.0) {
                        int finalI = i;
                        Platform.runLater(() -> _acm_martingaletierinfo.setText(String.format("Current Tier: #%d (%.8f). Next tier: %.8f",
                                finalI, martingaleTiers[finalI] / 100000000.0, martingaleTiers[finalI + 1] / 100000000.0)));
                        break;
                    }
                }
            } finally {
                Platform.runLater(() -> _acm_simulatemartingale.setDisable(false));
            }

        }).start();
    }

    public static boolean sim(double balance, double profit) {
        final double startBalance = balance;
        double amount = 0.00000001;
        boolean lastStat;
        while (true) {
            if (balance >= startBalance + profit) {
                return true;
            }
            if (amount > balance) {
                return false;
            }
            if (new Random().nextFloat() < 0.475) {
                balance += amount;
                lastStat = true;
            } else {
                balance -= amount;
                lastStat = false;
            }

            if (!lastStat) {
                amount = amount * 2;
            } else {
                amount = 0.00000001;
            }
        }
    }

    final private static int[] martingaleTiers = new int[]{1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191,
            16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431,
            67108863, 134217727, 268435455, 536870911, 1073741823, 2147483647};

}
