# Online Booking System

A professional, cross-platform appointment scheduling and reservation management desktop application built with **Java Swing**. It supports a Windows 11 style theme, Light, Dark, Red and Blue themes, and three interface languages (English, Persian, Chinese) with correct LTR/RTL text direction handling.

---

## 🇬🇧 English

### Overview
Online Booking System is a desktop application for managing appointments end-to-end: configurable business hours, services, customers and reservations. The operator enters the weekly opening/closing hours once, along with the desired slot duration and the buffer time between appointments — the application then automatically computes every open time slot for any date, and how long the wait is until the very next available slot. Written entirely in Java (Swing), it runs on Windows, Linux and macOS.

### Features
- Fully configurable **weekly business hours**: open time, close time, and a "closed" toggle for each individual day of the week
- Configurable **slot duration** and **buffer time** between appointments — the schedule is generated automatically from these two values, nothing is hardcoded
- Automatic **available time slot calculation** for any selected date and service
- Automatic **"next available slot" finder**, scanning forward across days and showing exactly how much time remains until it (days / hours / minutes)
- Full reservation lifecycle: Pending, Confirmed, Completed, Cancelled
- Service management: name, duration in minutes, price, active/inactive
- Customer directory with name, phone and email, reusable across reservations, or create a new customer inline while booking
- Reservation list with date filter, cancel and mark-as-completed actions
- Export all reservations to CSV
- Dashboard with live counters: today's reservations, upcoming reservations, active services, registered customers
- All data is saved automatically to disk (no external database required) and reloaded on next launch
- 5 visual themes: Windows 11, Light, Dark, Red, Blue — switchable at runtime
- 3 languages: English, Persian (فارسی), Chinese (中文) — switchable at runtime, with automatic Right-to-Left layout for Persian and Left-to-Right layout for English/Chinese
- All preferences (theme/language) are remembered between sessions

### Requirements
- **Java Development Kit (JDK) 17 or newer**
- Operating system: Windows 10/11, Linux, or macOS

### Required Libraries / Dependencies
This project uses **only the standard Java SE library** (Swing, `java.time`, `java.io` serialization, `java.util.prefs`, etc.) — there are **no external third-party libraries to install**. You only need a working JDK.

Install a JDK if you don't already have one:

```bash
# Ubuntu / Debian
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# Fedora
sudo dnf install -y java-17-openjdk-devel

# macOS (Homebrew)
brew install openjdk@17

# Windows (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### Installation & Build

**Option A — Build with Maven**
```bash
mvn clean package
java -jar target/online-booking-system.jar
```

**Option B — Compile manually with javac**
```bash
# From the project root folder
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.bookingsystem.Main
```

### First-Time Setup
1. Launch the application.
2. Open the **Business Hours** tab and set the opening/closing time for each day, mark any closed days, and choose the slot duration and buffer time.
3. Click **Save Business Hours**.
4. Add your services in the **Services** tab (name, duration, price).
5. Go to **New Reservation**, pick a service and a date — the available time slots and the next available slot are computed automatically.

### Project Structure
```
OnlineBookingSystem/
├── pom.xml
└── src/main/java/com/bookingsystem/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (MainWindow, Theme, ThemeManager)
    ├── gui/components/(DashboardPanel, NewReservationPanel, ReservationsPanel,
    │                   ServicesPanel, CustomersPanel, BusinessHoursPanel)
    ├── model/         (BusinessDay, BusinessHoursConfig, Service, Customer,
    │                   Reservation, ReservationStatus, TimeSlot, AppData)
    ├── service/       (ReservationService, SlotGenerator, DataStore)
    └── util/          (CsvExporter)
```

### Disclaimer
This application stores its data locally on the machine it runs on. Always keep backups of important business data.

---

## 🇮🇷 فارسی

### معرفی
سیستم رزرو آنلاین یک برنامه دسکتاپ برای مدیریت کامل نوبت‌ها است: ساعات کاری قابل تنظیم، خدمات، مشتریان و رزروها. کافیست مدیر یک‌بار ساعات باز و بسته شدن هفتگی، مدت هر نوبت و فاصله بین نوبت‌ها را وارد کند؛ برنامه به‌طور خودکار تمام زمان‌های آزاد برای هر تاریخ و همچنین مدت‌زمان باقی‌مانده تا نزدیک‌ترین زمان آزاد را محاسبه می‌کند. این برنامه به‌طور کامل با جاوا (Swing) نوشته شده و روی ویندوز، لینوکس و مک اجرا می‌شود.

### ویژگی‌ها
- تنظیم کامل **ساعات کاری هفتگی**: زمان باز شدن، زمان بسته شدن و گزینه «تعطیل» برای هر روز از هفته به‌صورت جداگانه
- تنظیم **مدت هر نوبت** و **فاصله بین نوبت‌ها** — برنامه زمان‌بندی را به‌طور کامل و خودکار از روی همین دو مقدار می‌سازد
- محاسبه خودکار **زمان‌های آزاد** برای هر تاریخ و خدمت انتخاب‌شده
- یافتن خودکار **نزدیک‌ترین زمان آزاد**، با جستجو در روزهای آینده و نمایش دقیق زمان باقی‌مانده تا آن (روز / ساعت / دقیقه)
- چرخه کامل رزرو: در انتظار، تایید شده، انجام شده، لغو شده
- مدیریت خدمات: نام، مدت زمان به دقیقه، قیمت، فعال/غیرفعال
- دفترچه مشتریان با نام، شماره تماس و ایمیل، قابل استفاده مجدد در رزروهای بعدی، یا امکان ایجاد مشتری جدید در همان لحظه رزرو
- لیست رزروها با فیلتر بر اساس تاریخ و امکان لغو یا علامت‌گذاری به‌عنوان انجام‌شده
- خروجی گرفتن از تمام رزروها به فرمت CSV
- داشبورد با شمارنده‌های زنده: رزروهای امروز، رزروهای پیش‌رو، خدمات فعال، مشتریان ثبت‌شده
- تمام داده‌ها به‌طور خودکار روی دیسک ذخیره می‌شوند (بدون نیاز به دیتابیس خارجی) و در اجرای بعدی بارگذاری می‌شوند
- ۵ پوسته بصری: ویندوز ۱۱، روشن، تاریک، قرمز، آبی — قابل تغییر در حین اجرا
- ۳ زبان: انگلیسی، فارسی، چینی — قابل تغییر در حین اجرا، با چیدمان خودکار راست‌به‌چپ برای فارسی و چپ‌به‌راست برای انگلیسی/چینی
- تمام تنظیمات (پوسته/زبان) بین اجراهای مختلف برنامه به خاطر سپرده می‌شود

### پیش‌نیازها
- **جاوا (JDK) نسخه ۱۷ یا جدیدتر**
- سیستم‌عامل: ویندوز ۱۰/۱۱، لینوکس یا مک

### کتابخانه‌ها و پیش‌نیازهای لازم
این پروژه فقط از **کتابخانه استاندارد جاوا** (Swing، `java.time`، سریال‌سازی `java.io`، `java.util.prefs` و غیره) استفاده می‌کند و **هیچ کتابخانه شخص‌ثالثی نیاز به نصب ندارد**. تنها کافیست یک JDK فعال روی سیستم داشته باشید.

اگر JDK ندارید، آن را نصب کنید:

```bash
# اوبونتو / دبیان
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# فدورا
sudo dnf install -y java-17-openjdk-devel

# مک (Homebrew)
brew install openjdk@17

# ویندوز (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### نصب و بیلد پروژه

**روش الف — بیلد با Maven**
```bash
mvn clean package
java -jar target/online-booking-system.jar
```

**روش ب — کامپایل دستی با javac**
```bash
# از داخل پوشه اصلی پروژه
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.bookingsystem.Main
```

### راه‌اندازی اولیه
۱. برنامه را اجرا کنید.
۲. تب **ساعات کاری** را باز کنید و زمان باز و بسته شدن هر روز را تنظیم کنید، روزهای تعطیل را مشخص کنید و مدت هر نوبت و فاصله بین نوبت‌ها را انتخاب کنید.
۳. روی **ذخیره ساعات کاری** کلیک کنید.
۴. خدمات خود را در تب **خدمات** اضافه کنید (نام، مدت زمان، قیمت).
۵. به تب **رزرو جدید** بروید، یک خدمت و تاریخ انتخاب کنید — زمان‌های آزاد و نزدیک‌ترین زمان آزاد به‌طور خودکار محاسبه می‌شوند.

### ساختار پروژه
```
OnlineBookingSystem/
├── pom.xml
└── src/main/java/com/bookingsystem/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (MainWindow, Theme, ThemeManager)
    ├── gui/components/(DashboardPanel, NewReservationPanel, ReservationsPanel,
    │                   ServicesPanel, CustomersPanel, BusinessHoursPanel)
    ├── model/         (BusinessDay, BusinessHoursConfig, Service, Customer,
    │                   Reservation, ReservationStatus, TimeSlot, AppData)
    ├── service/       (ReservationService, SlotGenerator, DataStore)
    └── util/          (CsvExporter)
```

### سلب مسئولیت
این برنامه داده‌های خود را به‌صورت محلی روی همان سیستمی که اجرا می‌شود ذخیره می‌کند. همیشه از داده‌های مهم کسب‌وکار خود نسخه پشتیبان تهیه کنید.

---

## 🇨🇳 中文

### 概述
在线预约系统是一款用于端到端管理预约的桌面应用程序：可配置的营业时间、服务项目、客户和预约。管理员只需一次性输入每周的营业时间、期望的时段时长以及预约之间的间隔时间，应用程序便会自动计算任意日期的所有可用时段，以及距离下一个可用时段还需等待多长时间。该应用完全使用 Java（Swing）编写，可在 Windows、Linux 和 macOS 上运行。

### 功能特性
- 完全可配置的**每周营业时间**：为一周中的每一天单独设置开始营业时间、结束营业时间以及「休息」开关
- 可配置的**时段时长**与**时段间隔** — 排班表完全根据这两个数值自动生成，没有任何硬编码
- 针对所选日期和服务，自动**计算可用时段**
- 自动**查找下一个可用时段**，向未来多天进行扫描，并精确显示距离该时段还剩多少时间（天 / 小时 / 分钟）
- 完整的预约生命周期：待处理、已确认、已完成、已取消
- 服务项目管理：名称、时长（分钟）、价格、启用/停用
- 客户名录，包含姓名、电话和邮箱，可在多次预约中复用，也可以在预约时直接创建新客户
- 预约列表支持按日期筛选，并可取消或标记为已完成
- 将所有预约记录导出为 CSV 文件
- 仪表盘实时显示：今日预约数、即将到来的预约数、启用的服务项目数、已登记客户数
- 所有数据自动保存到本地磁盘（无需外部数据库），下次启动时自动加载
- 5 种视觉主题：Windows 11、浅色、深色、红色、蓝色 — 可在运行时切换
- 3 种语言：英语、波斯语、中文 — 可在运行时切换，波斯语自动使用从右到左布局，英语/中文自动使用从左到右布局
- 所有偏好设置（主题/语言）会在会话之间自动保存

### 系统要求
- **Java 开发工具包（JDK）17 或更高版本**
- 操作系统：Windows 10/11、Linux 或 macOS

### 所需库/依赖项
该项目**仅使用标准 Java SE 库**（Swing、`java.time`、`java.io` 序列化、`java.util.prefs` 等）——**无需安装任何第三方库**。您只需要一个可用的 JDK。

如果尚未安装 JDK，请安装：

```bash
# Ubuntu / Debian
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# Fedora
sudo dnf install -y java-17-openjdk-devel

# macOS (Homebrew)
brew install openjdk@17

# Windows (winget)
winget install EclipseAdoptium.Temurin.17.JDK
```

### 安装与构建

**方式一 — 使用 Maven 构建**
```bash
mvn clean package
java -jar target/online-booking-system.jar
```

**方式二 — 使用 javac 手动编译**
```bash
# 在项目根目录下执行
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.bookingsystem.Main
```

### 首次使用设置
1. 启动应用程序。
2. 打开**营业时间**标签页，设置每天的开始/结束营业时间，标记休息日，并选择时段时长与时段间隔。
3. 点击**保存营业时间**。
4. 在**服务项目**标签页中添加您的服务（名称、时长、价格）。
5. 进入**新建预约**，选择服务和日期 — 可用时段与下一个可用时段将自动计算完成。

### 项目结构
```
OnlineBookingSystem/
├── pom.xml
└── src/main/java/com/bookingsystem/
    ├── Main.java
    ├── i18n/          (Language, LanguageManager)
    ├── gui/           (MainWindow, Theme, ThemeManager)
    ├── gui/components/(DashboardPanel, NewReservationPanel, ReservationsPanel,
    │                   ServicesPanel, CustomersPanel, BusinessHoursPanel)
    ├── model/         (BusinessDay, BusinessHoursConfig, Service, Customer,
    │                   Reservation, ReservationStatus, TimeSlot, AppData)
    ├── service/       (ReservationService, SlotGenerator, DataStore)
    └── util/          (CsvExporter)
```

### 免责声明
本应用程序将数据保存在运行它的本地计算机上。请务必定期备份重要的业务数据。
