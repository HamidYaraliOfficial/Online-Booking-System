package com.bookingsystem.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Central translation manager. Holds all UI strings for the three
 * supported languages and notifies listeners when the language changes
 * so the whole UI (and its LTR/RTL orientation) can refresh instantly.
 */
public final class LanguageManager {

    public interface LanguageChangeListener {
        void onLanguageChanged(Language newLanguage);
    }

    private static final LanguageManager INSTANCE = new LanguageManager();

    private final Map<Language, Map<String, String>> dictionaries = new HashMap<>();
    private final java.util.List<LanguageChangeListener> listeners = new java.util.ArrayList<>();
    private final Preferences prefs = Preferences.userNodeForPackage(LanguageManager.class);

    private Language currentLanguage;

    private LanguageManager() {
        buildEnglish();
        buildPersian();
        buildChinese();
        String saved = prefs.get("app.language", Language.ENGLISH.getCode());
        currentLanguage = Language.fromCode(saved);
    }

    public static LanguageManager getInstance() {
        return INSTANCE;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(Language language) {
        this.currentLanguage = language;
        prefs.put("app.language", language.getCode());
        for (LanguageChangeListener l : listeners) {
            l.onLanguageChanged(language);
        }
    }

    public void addListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }

    public String t(String key) {
        Map<String, String> dict = dictionaries.get(currentLanguage);
        if (dict != null && dict.containsKey(key)) {
            return dict.get(key);
        }
        return key;
    }

    public String[] dayNames() {
        return new String[]{
                t("day.monday"), t("day.tuesday"), t("day.wednesday"), t("day.thursday"),
                t("day.friday"), t("day.saturday"), t("day.sunday")
        };
    }

    private void put(Language lang, String key, String value) {
        dictionaries.computeIfAbsent(lang, k -> new HashMap<>()).put(key, value);
    }

    private void buildEnglish() {
        Language l = Language.ENGLISH;
        put(l, "app.title", "Online Booking System");
        put(l, "menu.file", "File");
        put(l, "menu.file.save", "Save Data");
        put(l, "menu.file.exportCsv", "Export Reservations (CSV)...");
        put(l, "menu.file.exit", "Exit");
        put(l, "menu.language", "Language");
        put(l, "menu.theme", "Theme");
        put(l, "menu.theme.windows11", "Windows 11");
        put(l, "menu.theme.light", "Light");
        put(l, "menu.theme.dark", "Dark");
        put(l, "menu.theme.red", "Red");
        put(l, "menu.theme.blue", "Blue");
        put(l, "menu.help", "Help");
        put(l, "menu.help.about", "About");

        put(l, "tab.newBooking", "New Reservation");
        put(l, "tab.reservations", "Reservations");
        put(l, "tab.services", "Services");
        put(l, "tab.customers", "Customers");
        put(l, "tab.hours", "Business Hours");
        put(l, "tab.dashboard", "Dashboard");

        put(l, "label.service", "Service");
        put(l, "label.customer", "Customer");
        put(l, "label.newCustomer", "New Customer");
        put(l, "label.date", "Date");
        put(l, "label.availableSlots", "Available Time Slots");
        put(l, "label.nextAvailable", "Next Available Slot");
        put(l, "label.notes", "Notes");
        put(l, "label.name", "Name");
        put(l, "label.phone", "Phone");
        put(l, "label.email", "Email");
        put(l, "label.duration", "Duration (minutes)");
        put(l, "label.price", "Price");
        put(l, "label.slotDuration", "Slot Duration (minutes)");
        put(l, "label.bufferTime", "Buffer Between Slots (minutes)");
        put(l, "label.open", "Open");
        put(l, "label.close", "Close");
        put(l, "label.closedDay", "Closed");
        put(l, "label.status", "Status");
        put(l, "label.start", "Start");
        put(l, "label.end", "End");
        put(l, "label.filterByDate", "Filter by Date");
        put(l, "label.allDates", "All Dates");
        put(l, "label.totalToday", "Today's Reservations");
        put(l, "label.totalUpcoming", "Upcoming Reservations");
        put(l, "label.totalServices", "Active Services");
        put(l, "label.totalCustomers", "Registered Customers");
        put(l, "label.timeUntilNext", "Time Until Next Available Slot");

        put(l, "day.monday", "Monday");
        put(l, "day.tuesday", "Tuesday");
        put(l, "day.wednesday", "Wednesday");
        put(l, "day.thursday", "Thursday");
        put(l, "day.friday", "Friday");
        put(l, "day.saturday", "Saturday");
        put(l, "day.sunday", "Sunday");

        put(l, "status.pending", "Pending");
        put(l, "status.confirmed", "Confirmed");
        put(l, "status.completed", "Completed");
        put(l, "status.cancelled", "Cancelled");

        put(l, "button.book", "Confirm Reservation");
        put(l, "button.refresh", "Refresh");
        put(l, "button.add", "Add");
        put(l, "button.edit", "Edit");
        put(l, "button.delete", "Delete");
        put(l, "button.cancel", "Cancel Reservation");
        put(l, "button.complete", "Mark Completed");
        put(l, "button.save", "Save");
        put(l, "button.saveHours", "Save Business Hours");
        put(l, "button.clear", "Clear");

        put(l, "dialog.confirmTitle", "Confirm");
        put(l, "dialog.confirmCancel", "Are you sure you want to cancel this reservation?");
        put(l, "dialog.confirmDelete", "Are you sure you want to delete this item?");
        put(l, "dialog.aboutTitle", "About Online Booking System");
        put(l, "dialog.aboutText", "Online Booking System\nVersion 1.0.0\n\nA professional cross-platform appointment scheduling and reservation manager.\nBuilt with Java Swing.");

        put(l, "status.ready", "Ready");
        put(l, "status.bookingCreated", "Reservation created successfully.");
        put(l, "status.noSlotSelected", "Please select an available time slot.");
        put(l, "status.noServiceSelected", "Please select a service.");
        put(l, "status.noCustomer", "Please select or create a customer.");
        put(l, "status.slotUnavailable", "This slot is no longer available. Please choose another.");
        put(l, "status.noSlotsAvailable", "No available slots for the selected date.");
        put(l, "status.closedOnDate", "The business is closed on the selected date.");
        put(l, "status.dataSaved", "Data saved successfully.");
        put(l, "status.invalidHours", "Closing time must be after opening time.");
        put(l, "status.fillRequiredFields", "Please fill in all required fields.");
        put(l, "status.exported", "Reservations exported successfully.");

        put(l, "column.date", "Date");
        put(l, "column.time", "Time");
        put(l, "column.customer", "Customer");
        put(l, "column.service", "Service");
        put(l, "column.duration", "Duration");
        put(l, "column.status", "Status");
        put(l, "column.phone", "Phone");
        put(l, "column.name", "Name");
        put(l, "column.email", "Email");
        put(l, "column.price", "Price");
        put(l, "column.day", "Day");
        put(l, "column.hours", "Hours");
    }

    private void buildPersian() {
        Language l = Language.PERSIAN;
        put(l, "app.title", "سیستم رزرو آنلاین");
        put(l, "menu.file", "فایل");
        put(l, "menu.file.save", "ذخیره داده‌ها");
        put(l, "menu.file.exportCsv", "خروجی گرفتن از رزروها (CSV)...");
        put(l, "menu.file.exit", "خروج");
        put(l, "menu.language", "زبان");
        put(l, "menu.theme", "پوسته");
        put(l, "menu.theme.windows11", "ویندوز 11");
        put(l, "menu.theme.light", "روشن");
        put(l, "menu.theme.dark", "تاریک");
        put(l, "menu.theme.red", "قرمز");
        put(l, "menu.theme.blue", "آبی");
        put(l, "menu.help", "راهنما");
        put(l, "menu.help.about", "درباره برنامه");

        put(l, "tab.newBooking", "رزرو جدید");
        put(l, "tab.reservations", "رزروها");
        put(l, "tab.services", "خدمات");
        put(l, "tab.customers", "مشتریان");
        put(l, "tab.hours", "ساعات کاری");
        put(l, "tab.dashboard", "داشبورد");

        put(l, "label.service", "خدمت");
        put(l, "label.customer", "مشتری");
        put(l, "label.newCustomer", "مشتری جدید");
        put(l, "label.date", "تاریخ");
        put(l, "label.availableSlots", "زمان‌های آزاد");
        put(l, "label.nextAvailable", "نزدیک‌ترین زمان آزاد");
        put(l, "label.notes", "یادداشت");
        put(l, "label.name", "نام");
        put(l, "label.phone", "شماره تماس");
        put(l, "label.email", "ایمیل");
        put(l, "label.duration", "مدت زمان (دقیقه)");
        put(l, "label.price", "قیمت");
        put(l, "label.slotDuration", "مدت هر نوبت (دقیقه)");
        put(l, "label.bufferTime", "فاصله بین نوبت‌ها (دقیقه)");
        put(l, "label.open", "زمان باز شدن");
        put(l, "label.close", "زمان بسته شدن");
        put(l, "label.closedDay", "تعطیل");
        put(l, "label.status", "وضعیت");
        put(l, "label.start", "شروع");
        put(l, "label.end", "پایان");
        put(l, "label.filterByDate", "فیلتر بر اساس تاریخ");
        put(l, "label.allDates", "همه تاریخ‌ها");
        put(l, "label.totalToday", "رزروهای امروز");
        put(l, "label.totalUpcoming", "رزروهای پیش‌رو");
        put(l, "label.totalServices", "خدمات فعال");
        put(l, "label.totalCustomers", "مشتریان ثبت‌شده");
        put(l, "label.timeUntilNext", "زمان باقی‌مانده تا نوبت بعدی");

        put(l, "day.monday", "دوشنبه");
        put(l, "day.tuesday", "سه‌شنبه");
        put(l, "day.wednesday", "چهارشنبه");
        put(l, "day.thursday", "پنج‌شنبه");
        put(l, "day.friday", "جمعه");
        put(l, "day.saturday", "شنبه");
        put(l, "day.sunday", "یکشنبه");

        put(l, "status.pending", "در انتظار");
        put(l, "status.confirmed", "تایید شده");
        put(l, "status.completed", "انجام شده");
        put(l, "status.cancelled", "لغو شده");

        put(l, "button.book", "تایید رزرو");
        put(l, "button.refresh", "بروزرسانی");
        put(l, "button.add", "افزودن");
        put(l, "button.edit", "ویرایش");
        put(l, "button.delete", "حذف");
        put(l, "button.cancel", "لغو رزرو");
        put(l, "button.complete", "علامت‌گذاری به‌عنوان انجام‌شده");
        put(l, "button.save", "ذخیره");
        put(l, "button.saveHours", "ذخیره ساعات کاری");
        put(l, "button.clear", "پاک کردن");

        put(l, "dialog.confirmTitle", "تایید");
        put(l, "dialog.confirmCancel", "آیا مطمئن هستید که می‌خواهید این رزرو را لغو کنید؟");
        put(l, "dialog.confirmDelete", "آیا مطمئن هستید که می‌خواهید این مورد را حذف کنید؟");
        put(l, "dialog.aboutTitle", "درباره سیستم رزرو آنلاین");
        put(l, "dialog.aboutText", "سیستم رزرو آنلاین\nنسخه 1.0.0\n\nیک ابزار حرفه‌ای و چندسکویی برای مدیریت وقت‌ها و رزروها.\nساخته‌شده با جاوا سوئینگ.");

        put(l, "status.ready", "آماده");
        put(l, "status.bookingCreated", "رزرو با موفقیت ثبت شد.");
        put(l, "status.noSlotSelected", "لطفا یک زمان آزاد انتخاب کنید.");
        put(l, "status.noServiceSelected", "لطفا یک خدمت انتخاب کنید.");
        put(l, "status.noCustomer", "لطفا یک مشتری انتخاب یا ایجاد کنید.");
        put(l, "status.slotUnavailable", "این زمان دیگر در دسترس نیست. لطفا زمان دیگری انتخاب کنید.");
        put(l, "status.noSlotsAvailable", "برای تاریخ انتخاب‌شده زمان آزادی وجود ندارد.");
        put(l, "status.closedOnDate", "در تاریخ انتخاب‌شده، کسب‌وکار تعطیل است.");
        put(l, "status.dataSaved", "داده‌ها با موفقیت ذخیره شد.");
        put(l, "status.invalidHours", "زمان بسته شدن باید بعد از زمان باز شدن باشد.");
        put(l, "status.fillRequiredFields", "لطفا تمام فیلدهای لازم را پر کنید.");
        put(l, "status.exported", "رزروها با موفقیت خروجی گرفته شدند.");

        put(l, "column.date", "تاریخ");
        put(l, "column.time", "زمان");
        put(l, "column.customer", "مشتری");
        put(l, "column.service", "خدمت");
        put(l, "column.duration", "مدت زمان");
        put(l, "column.status", "وضعیت");
        put(l, "column.phone", "شماره تماس");
        put(l, "column.name", "نام");
        put(l, "column.email", "ایمیل");
        put(l, "column.price", "قیمت");
        put(l, "column.day", "روز");
        put(l, "column.hours", "ساعات کاری");
    }

    private void buildChinese() {
        Language l = Language.CHINESE;
        put(l, "app.title", "在线预约系统");
        put(l, "menu.file", "文件");
        put(l, "menu.file.save", "保存数据");
        put(l, "menu.file.exportCsv", "导出预约记录 (CSV)...");
        put(l, "menu.file.exit", "退出");
        put(l, "menu.language", "语言");
        put(l, "menu.theme", "主题");
        put(l, "menu.theme.windows11", "Windows 11");
        put(l, "menu.theme.light", "浅色");
        put(l, "menu.theme.dark", "深色");
        put(l, "menu.theme.red", "红色");
        put(l, "menu.theme.blue", "蓝色");
        put(l, "menu.help", "帮助");
        put(l, "menu.help.about", "关于");

        put(l, "tab.newBooking", "新建预约");
        put(l, "tab.reservations", "预约列表");
        put(l, "tab.services", "服务项目");
        put(l, "tab.customers", "客户管理");
        put(l, "tab.hours", "营业时间");
        put(l, "tab.dashboard", "仪表盘");

        put(l, "label.service", "服务项目");
        put(l, "label.customer", "客户");
        put(l, "label.newCustomer", "新客户");
        put(l, "label.date", "日期");
        put(l, "label.availableSlots", "可预约时段");
        put(l, "label.nextAvailable", "最近可预约时段");
        put(l, "label.notes", "备注");
        put(l, "label.name", "姓名");
        put(l, "label.phone", "电话");
        put(l, "label.email", "邮箱");
        put(l, "label.duration", "时长（分钟）");
        put(l, "label.price", "价格");
        put(l, "label.slotDuration", "每个时段时长（分钟）");
        put(l, "label.bufferTime", "时段间隔（分钟）");
        put(l, "label.open", "开始营业");
        put(l, "label.close", "结束营业");
        put(l, "label.closedDay", "休息");
        put(l, "label.status", "状态");
        put(l, "label.start", "开始");
        put(l, "label.end", "结束");
        put(l, "label.filterByDate", "按日期筛选");
        put(l, "label.allDates", "所有日期");
        put(l, "label.totalToday", "今日预约");
        put(l, "label.totalUpcoming", "即将到来的预约");
        put(l, "label.totalServices", "启用的服务项目");
        put(l, "label.totalCustomers", "已登记客户");
        put(l, "label.timeUntilNext", "距离下一个可用时段");

        put(l, "day.monday", "星期一");
        put(l, "day.tuesday", "星期二");
        put(l, "day.wednesday", "星期三");
        put(l, "day.thursday", "星期四");
        put(l, "day.friday", "星期五");
        put(l, "day.saturday", "星期六");
        put(l, "day.sunday", "星期日");

        put(l, "status.pending", "待处理");
        put(l, "status.confirmed", "已确认");
        put(l, "status.completed", "已完成");
        put(l, "status.cancelled", "已取消");

        put(l, "button.book", "确认预约");
        put(l, "button.refresh", "刷新");
        put(l, "button.add", "添加");
        put(l, "button.edit", "编辑");
        put(l, "button.delete", "删除");
        put(l, "button.cancel", "取消预约");
        put(l, "button.complete", "标记为已完成");
        put(l, "button.save", "保存");
        put(l, "button.saveHours", "保存营业时间");
        put(l, "button.clear", "清除");

        put(l, "dialog.confirmTitle", "确认");
        put(l, "dialog.confirmCancel", "您确定要取消此预约吗？");
        put(l, "dialog.confirmDelete", "您确定要删除此项吗？");
        put(l, "dialog.aboutTitle", "关于在线预约系统");
        put(l, "dialog.aboutText", "在线预约系统\n版本 1.0.0\n\n一款专业的跨平台预约与日程管理工具。\n使用 Java Swing 构建。");

        put(l, "status.ready", "就绪");
        put(l, "status.bookingCreated", "预约创建成功。");
        put(l, "status.noSlotSelected", "请选择一个可用时段。");
        put(l, "status.noServiceSelected", "请选择一个服务项目。");
        put(l, "status.noCustomer", "请选择或创建一位客户。");
        put(l, "status.slotUnavailable", "该时段已不可用，请选择其他时段。");
        put(l, "status.noSlotsAvailable", "所选日期没有可用时段。");
        put(l, "status.closedOnDate", "所选日期为休息日。");
        put(l, "status.dataSaved", "数据保存成功。");
        put(l, "status.invalidHours", "结束时间必须晚于开始时间。");
        put(l, "status.fillRequiredFields", "请填写所有必填字段。");
        put(l, "status.exported", "预约记录导出成功。");

        put(l, "column.date", "日期");
        put(l, "column.time", "时间");
        put(l, "column.customer", "客户");
        put(l, "column.service", "服务项目");
        put(l, "column.duration", "时长");
        put(l, "column.status", "状态");
        put(l, "column.phone", "电话");
        put(l, "column.name", "姓名");
        put(l, "column.email", "邮箱");
        put(l, "column.price", "价格");
        put(l, "column.day", "星期");
        put(l, "column.hours", "营业时间");
    }
}
