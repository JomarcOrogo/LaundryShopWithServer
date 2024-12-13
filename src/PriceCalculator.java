public class PriceCalculator {
    public static double calculatePrice(String packageType, double weight) {
        double baseWeight = packageType.equals("Basic Package") ? 7 : 9;
        double basePrice = 150;
        double premiumCharge = packageType.equals("Premium Package") ? 50 : 0;
        double extraCharge = Math.max(0, weight - baseWeight) * 15;
        return basePrice + premiumCharge + extraCharge;
    }
}
