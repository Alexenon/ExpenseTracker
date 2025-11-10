package com.example.application.entities;

import com.example.application.utils.common.formatters.CommonFormatters;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Data
@Entity(name = "expenses")
@EqualsAndHashCode(of = {"id", "name", "category", "user"})
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @Column(name = "expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Column(name = "timestamp", nullable = false)
    @Enumerated(EnumType.STRING)
    private Timestamp timestamp = Timestamp.ONCE;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum Timestamp {
        ONCE,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY;

        public static List<String> getTimestampNames() {
            return Arrays.stream(values()).map(Timestamp::name).toList();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Expense.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("amount=" + amount)
                .add("description='" + description + "'")
                .add("startDate=" + (startDate == null ? "N/A" : CommonFormatters.DATE.format(startDate)))
                .add("expireDate=" + (expireDate == null ? "N/A" : CommonFormatters.DATE.format(expireDate)))
                .add("timestamp=" + timestamp)
                .add("category=" + category)
                .add("user=" + user)
                .toString();
    }

}

