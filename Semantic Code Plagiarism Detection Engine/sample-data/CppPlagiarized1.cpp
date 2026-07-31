#include <iostream>

void sort_elements(int values[], int count) {
    /* Perform standard bubble sort */
    for (int outer = 0; outer < count - 1; outer++) {
        for (int inner = 0; inner < count - outer - 1; inner++) {
            if (values[inner] > values[inner + 1]) {
                // Perform structural swap
                int swap_var = values[inner];
                values[inner] = values[inner + 1];
                values[inner + 1] = swap_var;
            }
        }
    }
}
