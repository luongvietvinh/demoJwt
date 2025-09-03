package com.example.demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import com.example.demo.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Common utility class providing helper methods for:
 *  - String processing
 *  - Number operations
 *  - Date & validation
 *  - Collections & partition
 *  - HTTP headers & file handling
 *
 * Lớp tiện ích chung cung cấp các hàm hỗ trợ:
 *  - Xử lý chuỗi
 *  - Tính toán số học
 *  - Kiểm tra dữ liệu
 *  - Làm việc với Collection
 *  - HTTP headers & đọc file
 */
public final class CommonUtils {

    private CommonUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // ============================== 🔐 Security ==============================

    /**
     * Get the current logged-in user from Spring Security context.
     * Lấy thông tin user hiện tại từ Spring Security context.
     *
     * @return User principal object
     */
    public static CustomUserDetails getUserLogin() {
      return (CustomUserDetails) SecurityContextHolder.getContext()
              .getAuthentication()
              .getPrincipal();
  }

    // ============================== ✅ Validation ==============================

    /**
     * Validate a target object using a Spring Validator.
     * Validate (kiểm tra hợp lệ) một object bằng Spring Validator.
     *
     * @param target    object to validate (đối tượng cần validate)
     * @param validator validator implementation (bộ validator)
     * @return BindingResult containing validation errors if any
     *         BindingResult chứa lỗi validate (nếu có)
     */
    public static BindingResult validate(Object target, Validator validator) {
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "");
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        springValidator.validate(target, bindingResult);
        return bindingResult;
    }

    /**
     * Build default Hibernate Validator instance.
     * Tạo Hibernate Validator mặc định.
     *
     * @return javax.validation.Validator
     */
    public static jakarta.validation.Validator buildValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    // ============================== 🔤 String ==============================

    /**
     * Replace placeholders in a text with values from a map.
     * Thay thế placeholder trong chuỗi bằng giá trị từ Map.
     */
    public static String replaceMap(String bodyText, Map<String, String> mapParams) {
        if (MapUtils.isEmpty(mapParams)) {
            return bodyText;
        }
        for (Map.Entry<String, String> entry : mapParams.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            bodyText = bodyText.replace(entry.getKey(), value);
        }
        return bodyText;
    }

    /**
     * Check if a string contains HTML tags.
     * Kiểm tra chuỗi có chứa thẻ HTML không.
     */
    public static boolean isHtml(String input) {
        if (input != null) {
            Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
            return htmlPattern.matcher(input).matches();
        }
        return false;
    }

    /**
     * Verify if a string is Juridical Code (13 digits + checksum).
     * Kiểm tra chuỗi có phải Juridical Code hợp lệ hay không (13 ký tự + mã kiểm tra).
     */
    public static boolean isValidJuridicalCode(String input) {
        if (!StringUtils.isEmpty(input) && 13 == input.length()) {
            int even = 0;
            int odd = 0;
            for (int i = 1; i < input.length(); i++) {
                if (i % 2 == 0) {
                    odd += Character.getNumericValue(input.charAt(i));
                } else {
                    even += Character.getNumericValue(input.charAt(i));
                }
            }
            Integer firstCode = 9 - ((even * 2 + odd) % 9);
            return firstCode.equals(Character.getNumericValue(input.charAt(0)));
        }
        return false;
    }

    // ============================== 🔢 Number ==============================

    /**
     * Format number with given pattern.
     * Định dạng số theo pattern cho trước.
     */
    public static String formatNumberWithPattern(String number, String pattern) {
        if (StringUtils.isEmpty(number)) {
            return StringUtils.EMPTY;
        }
        try {
            DecimalFormat formatter = new DecimalFormat(pattern);
            return formatter.format(Double.parseDouble(number));
        } catch (Exception ex) {
            ex.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * Format integer with given pattern.
     * Định dạng số nguyên theo pattern cho trước.
     */
    public static String formatNumberWithPattern(Integer number, String pattern) {
        return String.format(pattern, number);
    }

    /**
     * Convert Excel column name (A,B,C...) to number.
     * Chuyển tên cột Excel (A,B,C...) thành số.
     */
    public static int toExcelNumber(String name) {
        int number = 0;
        for (int i = 0; i < name.length(); i++) {
            number = number * 26 + (name.charAt(i) - ('A' - 1));
        }
        return number;
    }

    /**
     * Calculate tax based on membership fee.
     * Tính thuế dựa trên phí thành viên.
     */
    public static Integer calculateTax(Integer annualMembershipFee) {
        return Math.round(annualMembershipFee
                * Constants.TAX_IN_PERCENT / Constants.MAX_HUNDRED_PERCENT);
    }

    /**
     * Round double to specific scale with rounding mode.
     * Làm tròn số thực đến số chữ số thập phân mong muốn.
     */
    public static double roundUpDouble(double number, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(number)
                .setScale(scale, roundingMode)
                .doubleValue();
    }

    /**
     * Sum double numbers accurately using BigDecimal.
     * Cộng chính xác các số thực bằng BigDecimal.
     */
    public static double sumDoubleExactValue(Double... numbers) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (Double num : numbers) {
            if (!Objects.isNull(num)) {
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(num));
            }
        }
        return bigDecimal.doubleValue();
    }

    /**
     * Round number and format as percentage with 1 decimal.
     * Làm tròn và định dạng số thành phần trăm với 1 chữ số thập phân.
     */
    public static String roundAndFormatPercent1DecimalToString(Double number) {
        if (Objects.isNull(number)) {
            return "";
        }
        if (0 == number) {
            return "0.0";
        }
        return String.valueOf(Math.round(number * 10) / 10.0);
    }

    /**
     * Format decimal number with N digits after comma.
     * Định dạng số thực với N chữ số thập phân sau dấu phẩy.
     */
    public static String formatDecimal(Double number, int numberOfDecimal) {
        numberOfDecimal = Math.max(1, numberOfDecimal);
        String numberFormatPattern = "%." + numberOfDecimal + "f";
        if (Objects.isNull(number) || 0 == number) {
            return String.format(numberFormatPattern, 0.0);
        }
        return String.format(numberFormatPattern, number);
    }

    /**
     * Convert bytes to kilobytes.
     * Chuyển đổi từ bytes sang kilobytes.
     */
    public static long convertBytesToKb(long bytes) {
        return bytes / Constants.ONE_KILOBYTE;
    }

    /**
     * Try to parse string to Integer safely.
     * Chuyển chuỗi sang số nguyên, nếu lỗi thì trả về null.
     */
    public static Integer tryParse(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ============================== 📑 Collections ==============================

    /**
     * Partition a list into sublists of given size.
     * Chia List thành các List con với kích thước cho trước.
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    /**
     * Partition a HashMap into smaller HashMaps of given size.
     * Chia HashMap thành các HashMap nhỏ hơn theo kích thước cho trước.
     */
    public static <K, V> List<HashMap<K, V>> partition(HashMap<K, V> map, int size) {
        List<HashMap<K, V>> partitions = new ArrayList<>();
        List<K> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i += size) {
            List<K> sublist = keys.subList(i, Math.min(i + size, keys.size()));
            HashMap<K, V> newMap = new HashMap<>(map);
            newMap.keySet().retainAll(sublist);
            partitions.add(newMap);
        }
        return partitions;
    }

    /**
     * Distinct elements in stream by key extractor.
     * Lọc phần tử khác nhau trong stream theo key.
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Get item in list safely by index.
     * Lấy phần tử trong List an toàn bằng index.
     */
    public static Object getItemInList(List<?> list, int index) {
        try {
            if (index < 0 || index >= list.size()) {
                return null;
            }
            return list.get(index);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    // ============================== 🌐 HTTP & File ==============================

    /**
     * Build HTTP headers for file download.
     * Tạo HTTP headers cho việc tải file về.
     */
    public static HttpHeaders getHeaderDownload(String fileName) {
        HttpHeaders header = new HttpHeaders();
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return header;
    }

    /**
     * Encode file name for HTTP header.
     * Mã hóa tên file để set trong HTTP header.
     */
    public static String encodeHeaderValues(String filename) {
        String encodedFileName =
                URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("attachment; filename=%s; filename*=utf-8''%s",
                encodedFileName,
                encodedFileName);
    }

    /**
     * Read file resource by pattern.
     * Đọc file resource bằng pattern (mẫu đường dẫn).
     */
    public static InputStream readFile(String resourceTemplate) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(resourceTemplate);
            if (resources.length == 0) {
                return null;
            }
            return resources[0].getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    // ============================== ⚙️ ObjectMapper ==============================

    /**
     * Configure Jackson ObjectMapper with timezone, naming strategy, custom serializers.
     * Cấu hình Jackson ObjectMapper với timezone, naming strategy, và serializer tùy chỉnh.
     */
    public static void setUpObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setTimeZone(TimeZone.getDefault())
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new CustomStringDeserializer());
        module.addDeserializer(Timestamp.class, new CustomTimestampDeserializer());
        objectMapper.registerModule(module);
    }

    // ============================== 🔗 Other ==============================

    /**
     * Handle multiple AND conditions, return true if all are true.
     * Kiểm tra nhiều điều kiện AND, trả về true nếu tất cả đều đúng.
     */
    public static boolean handleMultipleAndConditions(Boolean... conditions) {
        if (0 == conditions.length) {
            return true;
        }
        Set<Boolean> conditionResults = new HashSet<>(List.of(conditions));
        return !conditionResults.contains(Boolean.FALSE);
    }
}
