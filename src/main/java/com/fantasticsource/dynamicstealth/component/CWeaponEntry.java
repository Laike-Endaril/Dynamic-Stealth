package com.fantasticsource.dynamicstealth.component;

import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import com.fantasticsource.mctools.component.CFantasticPotionEffect;
import com.fantasticsource.mctools.component.CItemFilter;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CWeaponEntry extends Component
{
    public WeaponEntry value = null;

    public CWeaponEntry set(WeaponEntry value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CWeaponEntry write(ByteBuf buf)
    {
        CFantasticPotionEffect ceffect = new CFantasticPotionEffect();

        new CItemFilter().set(value.filter).write(buf);

        buf.writeBoolean(value.consumeItem);
        buf.writeBoolean(value.armorPenetration);
        buf.writeDouble(value.damageMultiplier);

        buf.writeInt(value.attackerEffects.size());
        for (FantasticPotionEffect effect : value.attackerEffects) ceffect.set(effect).write(buf);

        buf.writeInt(value.victimEffects.size());
        for (FantasticPotionEffect effect : value.victimEffects) ceffect.set(effect).write(buf);

        return this;
    }

    @Override
    public CWeaponEntry read(ByteBuf buf)
    {
        CFantasticPotionEffect ceffect = new CFantasticPotionEffect();

        value = new WeaponEntry(new CItemFilter().read(buf).value);

        value.consumeItem = buf.readBoolean();
        value.armorPenetration = buf.readBoolean();
        value.damageMultiplier = buf.readDouble();

        for (int i = buf.readInt(); i > 0; i++) value.attackerEffects.add(ceffect.read(buf).value);
        for (int i = buf.readInt(); i > 0; i++) value.victimEffects.add(ceffect.read(buf).value);

        return this;
    }

    @Override
    public CWeaponEntry save(OutputStream stream)
    {
        CInt ci = new CInt();
        CFantasticPotionEffect ceffect = new CFantasticPotionEffect();

        new CItemFilter().set(value.filter).save(stream);

        new CBoolean().set(value.consumeItem).save(stream).set(value.armorPenetration).save(stream);
        new CDouble().set(value.damageMultiplier).save(stream);

        ci.set(value.attackerEffects.size()).save(stream);
        for (FantasticPotionEffect effect : value.attackerEffects) ceffect.set(effect).save(stream);

        ci.set(value.victimEffects.size()).save(stream);
        for (FantasticPotionEffect effect : value.victimEffects) ceffect.set(effect).save(stream);

        return this;
    }

    @Override
    public CWeaponEntry load(InputStream stream)
    {
        CBoolean cb = new CBoolean();
        CInt ci = new CInt();
        CFantasticPotionEffect ceffect = new CFantasticPotionEffect();

        value.filter = new CItemFilter().load(stream).value;

        value.consumeItem = cb.load(stream).value;
        value.armorPenetration = cb.load(stream).value;
        value.damageMultiplier = new CDouble().load(stream).value;

        for (int i = ci.load(stream).value; i > 0; i++) value.attackerEffects.add(ceffect.load(stream).value);
        for (int i = ci.load(stream).value; i > 0; i++) value.victimEffects.add(ceffect.load(stream).value);

        return this;
    }
}
