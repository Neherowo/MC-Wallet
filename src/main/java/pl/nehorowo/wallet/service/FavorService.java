package pl.nehorowo.wallet.service;

import lombok.Getter;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.module.Service;

import java.util.HashSet;
import java.util.Set;


//ServicesService XDDDDDDDDDDDDDDDDD - ik about Favour is correct but favor is also correct :)
@Getter
public class FavorService {

    private static FavorService instance = new FavorService();
    private final Set<Service> serviceSet = new HashSet<>();

    public static FavorService getInstance() {
        if(instance == null) instance = new FavorService();
        return instance;
    }

    public int injectKits() {
        Set<Service> services = WalletPlugin.getInstance().getItemsConfiguration().getServices();

        serviceSet.addAll(services);

        return services.size();
    }
}
