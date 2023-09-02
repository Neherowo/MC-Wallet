package pl.nehorowo.wallet.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import pl.nehorowo.wallet.module.Service;

public class ServiceSerializer implements ObjectSerializer<Service> {
    public boolean supports(Class<? super Service> type) {
        return Service.class.isAssignableFrom(type);
    }

    public void serialize(@NonNull Service object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("item", object.getItem());
        data.add("slot", object.getSlot());
        data.add("price", object.getPrice());
        data.add("command", object.getCommand());
    }

    public Service deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        return new Service(
                data.get("item", ItemStack.class),
                data.get("slot", Integer.class),
                data.get("price", Integer.class),
                data.get("command", String.class)
        );
    }
}
