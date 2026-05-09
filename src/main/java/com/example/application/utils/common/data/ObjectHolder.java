package com.example.application.utils.common.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjectHolder<T> {

	private T value;

}
