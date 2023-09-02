package pl.nehorowo.wallet;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pl.nehorowo.wallet.commands.AWalletCommand;
import pl.nehorowo.wallet.commands.WalletCommand;
import pl.nehorowo.wallet.configuration.Configuration;
import pl.nehorowo.wallet.configuration.MessageConfiguration;
import pl.nehorowo.wallet.configuration.ServiceItemsConfiguration;
import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.database.DatabaseConfiguration;
import pl.nehorowo.wallet.database.DatabaseConnector;
import pl.nehorowo.wallet.database.serializer.UUIDSerializer;
import pl.nehorowo.wallet.database.serializer.UserControllerSerializer;
import pl.nehorowo.wallet.listener.JoinQuitListener;
import pl.nehorowo.wallet.notice.NoticeSerializer;
import pl.nehorowo.wallet.placeholder.Placeholder;
import pl.nehorowo.wallet.serializer.ServiceSerializer;
import pl.nehorowo.wallet.service.FavorService;
import pl.nehorowo.wallet.service.UserService;
import pl.nehorowo.wallet.task.UpdateTask;
import pl.nehorowo.wallet.util.TextUtil;

import java.lang.reflect.Field;
import java.util.List;

@Getter
public class WalletPlugin extends JavaPlugin {

    @Getter private static WalletPlugin instance;

    private DatabaseConnector connector;

    private MessageConfiguration messageConfiguration;
    private Configuration configuration;
    private ServiceItemsConfiguration itemsConfiguration;

    private InventoryManager inventoryManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerConfiguration();

        connector = new DatabaseConnector(new DatabaseConfiguration(
                getConfiguration().getHost(),
                getConfiguration().getUsername(),
                getConfiguration().getPassword(),
                getConfiguration().getDatabase(),
                getConfiguration().getPort(),
                getConfiguration().isSsl()
        ));

        connector.registerSerializer(new UserControllerSerializer());
        connector.registerSerializer(new UUIDSerializer());
        connector.registerDataObjectToScan(UserController.class);

        connector.getScanner(UserController.class)
                .ifPresent(userControllerDataObjectScanner ->
                        userControllerDataObjectScanner.load(UserService.getInstance())
                );

        registerCommands();
        registerListeners();
        registerTasks();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new Placeholder().register();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        int i = FavorService.getInstance().injectKits();
        TextUtil.sendLogger("Zaladowano " + i + " uslug.");
    }

    @Override @SneakyThrows
    public void onDisable() {
        if(connector.getConnection() != null) connector.getConnection().close();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new Placeholder().unregister();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getOnlinePlayers().forEach(player ->
          UserService.getInstance().get(player.getUniqueId()).ifPresent(userController -> {
            userController.setPlayer(null);
            userController.update();
        }));

        itemsConfiguration.load();
        int i = FavorService.getInstance().injectKits();
        TextUtil.sendLogger("Trwa zapisywanie " + i + " uslug.");

        itemsConfiguration.getServices().clear();
        itemsConfiguration.getServices().addAll(FavorService.getInstance().getServiceSet());
        itemsConfiguration.save();
    }

    /*
        Registering listeners, commands, tasks and configuration
     */

    private void registerListeners() {
        List.of(
                new JoinQuitListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @SneakyThrows
    private void registerCommands() {
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        final CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());

        List.of(
                new WalletCommand(),
                new AWalletCommand()
        ).forEach(commands ->
                commandMap.register("mc-wallet", commands)
        );
    }

    private void registerTasks() {
        List.of(
                new UpdateTask()
        ).forEach(task ->
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, 20L, 20L * 60L) //xpp
        );
    }

    private void registerConfiguration() {
        configuration = ConfigManager.create(Configuration.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/configuration.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        messageConfiguration = ConfigManager.create(MessageConfiguration.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/messages.yml");
            it.withSerdesPack(registry -> registry.register(new NoticeSerializer()));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        itemsConfiguration = ConfigManager.create(ServiceItemsConfiguration.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/services.yml");
            it.withSerdesPack(registry -> registry.register(new ServiceSerializer()));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }
}
