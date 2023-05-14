package com.justixdev.eazynick.nms.netty;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import lombok.Getter;

import java.net.InetAddress;

public abstract class PacketInjector {

    protected EazyNick eazyNick;
    protected Utils utils;
    protected SetupYamlFile setupYamlFile;
    @Getter
    protected InetAddress address;
    @Getter
    protected InjectorType type;
    protected String handlerName;

    public PacketInjector(InetAddress address, InjectorType type) {
        this.eazyNick = EazyNick.getInstance();
        this.utils = this.eazyNick.getUtils();
        this.setupYamlFile = this.eazyNick.getSetupYamlFile();
        this.address = address;
        this.type = type;
        this.handlerName = this.eazyNick.getName().toLowerCase() + "_" + type.getHandlerName();
    }

    public abstract void inject();

    public abstract void unregisterChannel();

    public boolean onPacketReceive(Object packet) {
        return true;
    }

    public Object onPacketSend(Object packet) {
        return packet;
    }

    public void remove() {
        Object lock = new Object();
        Thread killThread = new Thread(() -> {
            try {
                this.unregisterChannel();
            } catch (Exception ignore) {
            }

            synchronized (lock) {
                lock.notify();
            }
        });
        killThread.start();

        synchronized (lock) {
            try {
                lock.wait(50);
            } catch (InterruptedException ignore) {
            }
        }

        if(killThread.isAlive())
            killThread.interrupt();
    }

}
