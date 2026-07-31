#include <vector>

std::vector<std::vector<int>> transpose(const std::vector<std::vector<int>>& matrix) {
    if (matrix.empty()) return {};
    int rows = matrix.size();
    int cols = matrix[0].size();
    std::vector<std::vector<int>> result(cols, std::vector<int>(rows));

    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < cols; ++j) {
            result[j][i] = matrix[i][j];
        }
    }
    return result;
}
