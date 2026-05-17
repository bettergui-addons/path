# 🧩 Path — Custom Paths Support for BetterGUI

**Path** is an addon for [BetterGUI](https://bettergui-mc.github.io/Docs/index.html) that allows you to define **custom folder paths** for loading menus and templates. This is useful for modular setups, organizing menus by category, or separating addon-specific GUIs.

---

## 📦 Features

- ✅ Load menus from **additional directories**
- ✅ Load templates (`type: template`) from **custom folders**
- ✅ Supports both **absolute** and **relative** paths
- ✅ Ignores path if set to `none`
- ✅ Compatible with Folia-style scheduling through BetterGUI

---

---

## ⚙️ Configuration

After the first run, a `config.yml` file will be generated.

### Example:

```yaml
menu-paths:
  - menus
  - extra-menus
  - /absolute/path/to/menus
  - none

template-paths:
  - templates
  - custom-templates
  - /absolute/path/to/templates
  - none
```

- `menu-paths`: list of folders where additional menus (`.yml` files) will be loaded.
- `template-paths`: list of folders where template files are located.
- **Relative paths** are resolved against `plugins/BetterGUI`.
- **Absolute paths** (starting with `/` or `\\`) are loaded directly.
- **If a path is set to `none`**, it will be ignored completely.

---

## 🔧 Installation

1. Download `Path.jar`
2. Place it into: `plugins/BetterGUI/addons`
3. Restart the server or use:

```bash
/rlplugin
```

---

## 🧑‍💻 Author

> Developed [GIGABAIT](https://github.com/gigabait93)  
> Part of the **[TensaCraft](https://tensa.co.ua)** ecosystem

---

## 📜 License

This project is licensed under the [MIT License](LICENSE)
