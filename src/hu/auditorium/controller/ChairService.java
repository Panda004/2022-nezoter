package hu.auditorium.controller;

import hu.auditorium.model.domain.Chair;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChairService {

    private static final int ROW_MAX = 15;
    private static final int NUMBER_MAX = 20;
    private final List<Chair> chairs;

    public ChairService(List<Chair> chairs) {
        this.chairs = chairs;
    }

    public String isGivenChairOccupied(int row, int number){
        return String.format("A megadott szék %s", isOccupied(row, number) ? "már foglalt." : "még üres.");
    }

    private boolean isOccupied(int row, int number){
        return chairs.stream()
                .filter(chair -> chair.isChair(row, number))
                .findAny()
                .map(Chair::isOccupied)
                .orElse(true);
    }

    public String getStatistic(){
        long occupideChairs = countOccupiedChairs();
        double percent = occupideChairs * 100.0 / (ROW_MAX * NUMBER_MAX);
        return String.format("Az előadásra eddig %d jegyet adtak el, ez a nézőtér %.0f%%-a.",
                occupideChairs, percent);
    }

    private long   countOccupiedChairs(){
        return chairs.stream()
                .filter(Chair::isOccupied)
                .count();
    }

    public int getMostPopularCategoryId() {
        return getChairCategoryMap().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .get();
    }

    private Map<Integer, Long> getChairCategoryMap() {
        return chairs.stream()
                .filter(Chair::isOccupied)
                .collect(Collectors.groupingBy(Chair::getCategoryId, Collectors.counting()));
    }

    public int countTotalIncome() {
        return chairs.stream()
                .filter(Chair::isOccupied)
                .mapToInt(Chair::getPrice)
                .sum();
    }
    public long countSingleFreeChairs() {
        return chairs.stream()
                .filter(this::isSingleFreeChair)
                .count();
    }

    private boolean isSingleFreeChair(Chair chair){
        return !chair.isOccupied() &&
                isOccupied(chair.getRow(), chair.getNumber() - 1) &&
                isOccupied(chair.getRow(), chair.getNumber() + 1);
    }

    public List<String> getAuditoriumStatus(){
        String auditoriumsStatusInRow = getAuditoriumsStatusInRow();
        return IntStream.range(0, ROW_MAX)
                .mapToObj(row -> printChairsInRow(auditoriumsStatusInRow, row))
                .collect(Collectors.toList());
    }

    private String getAuditoriumsStatusInRow(){
        return chairs.stream()
                .map(Chair::toString)
                .collect(Collectors.joining());
    }
     private  String printChairsInRow(String auditoriumStatusInRow, int row) {
        return auditoriumStatusInRow.substring(row * NUMBER_MAX, (row + 1) * NUMBER_MAX);
     }



}
