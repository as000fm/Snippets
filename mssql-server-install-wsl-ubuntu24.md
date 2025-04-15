# 📘 Installing Microsoft SQL Server 2019 on WSL Ubuntu 24.04.2 LTS

This guide explains how to install **Microsoft SQL Server 2019** on **WSL (Windows Subsystem for Linux)** running **Ubuntu 24.04.2 LTS**.

> ⚠️ Official support for Ubuntu 24.04 is not yet guaranteed for SQL Server 2019. These steps assume compatibility similar to 22.04.

---

## ✅ Prerequisites

1. WSL 2 with Ubuntu 24.04.2 LTS installed.
2. Run `wsl --version` and ensure version is 1.1.3 or higher.
3. Internet connection.

---

## 🧰 Step 1: Import Microsoft’s GPG key and add the repository

```bash
curl -sSL https://packages.microsoft.com/keys/microsoft.asc | sudo tee /etc/apt/trusted.gpg.d/microsoft.asc
sudo add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/22.04/mssql-server-2019.list)"
```

---

## 🔄 Step 2: Update package list and install SQL Server

```bash
sudo apt update
sudo apt install -y mssql-server
```

> 💡 If you get dependency issues, replace `22.04` in the source list with `20.04` (fallback approach).

---

## ⚙️ Step 3: Configure SQL Server

After installation, run the setup tool:

```bash
sudo /opt/mssql/bin/mssql-conf setup
```

Choose:

- Edition (e.g., Developer)
- Accept license terms
- Set `sa` password

---

## ▶️ Step 4: Start and verify the service

```bash
systemctl status mssql-server
```

> If `systemctl` doesn’t work in WSL, use:

```bash
ps -ef | grep mssql
```

---

## 🧪 Step 5: Install SQL Server command-line tools (optional)

```bash
sudo apt install -y curl apt-transport-https
curl -sSL https://packages.microsoft.com/keys/microsoft.asc | sudo tee /etc/apt/trusted.gpg.d/microsoft.asc
sudo add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/22.04/prod.list)"
sudo apt update
sudo apt install -y mssql-tools unixodbc-dev
```

Add tools to PATH:

```bash
echo 'export PATH="$PATH:/opt/mssql-tools/bin"' >> ~/.bashrc
source ~/.bashrc
```

---

## 🧼 Troubleshooting

- If `systemd` is required and unavailable, consider using `genie` or `wsl-init` workarounds.
- If ports aren’t accessible from Windows, run:
  ```bash
  sudo ufw allow 1433/tcp
  ```

---

## 📍 To connect from Windows

Use SSMS (SQL Server Management Studio) or Azure Data Studio with:

- Server: `localhost,1433`
- User: `sa`
- Password: (your password)

---

## 📦 Uninstall (optional)

```bash
sudo apt remove --purge mssql-server
```

---

© 2025 - SQL Server on WSL Guide
