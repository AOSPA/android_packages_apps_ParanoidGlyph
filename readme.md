# Paranoid Android Doze #

## Setting up ParanoidDoze ##

### Syncing ParanoidDoze ###
You can sync ParanoidDoze by adding it to your pa.dependencies
```bash
    {
        "remote":       "aospa",
        "repository":   "AOSPA/android_packages_apps_paranoiddoze",
        "target_path":  "packages/apps/ParanoidDoze",
        "revision":     "quartz"
    }
```
### Adding ParanoidDoze to your device tree ###
To build ParanoidDoze you have to add the following lines to your device.mk
```bash
    # ParanoidDoze
    PRODUCT_PACKAGES += ParanoidDoze
```
In order to have the proper options showing up you have to define the device related sensors in your build properties
```bash
    ro.sensor.proximity # Can be "true" if the device uses the proximity sensor to check for pocket & handwave gestures
    ro.sensor.pocket    # Can be the device specific pocket sensor to enable pocket gesture
    ro.sensor.pickup    # Can be the device specific pickup sensor to enable pickup gesture
```

#### Examples ####
Xiaomi devices usually use the following sensors (please verify on your own if these work or not)
```bash
    ro.sensor.proximity=true
    ro.sensor.pickup=xiaomi.sensor.pickup
```

Oneplus devices usually use the following sensors (please verify on your own if these work or not)
```bash
    ro.sensor.pocket=oneplus.sensor.pocket
    ro.sensor.pickup=oneplus.sensor.pickup
```