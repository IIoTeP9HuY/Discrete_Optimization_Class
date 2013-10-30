#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <cmath>
#include <vector>
#include <random>
#include <cassert>

using namespace std;

random_device rd;
mt19937 gen(rd());

int readInput(char* fileName)
{
  FILE* input = fopen(fileName, "r");

  int n;
  fscanf(input, "%d", &n);
  return n;
}

int randomInt(int n)
{
  return rand() % n;
}

struct Solution
{
  Solution(long long conflictsNumber, const vector<int>& queens): 
    conflictsNumber(conflictsNumber),
    queens(queens) {}

  long long conflictsNumber;
  vector<int> queens;
};

void printSolution(const Solution& solution)
{
  for (int i = 0; i < solution.queens.size(); ++i)
  {
    printf("%d ", solution.queens[i]);
  }
  printf("\n");
}


int hasConflict(const vector<int>& solution, int firstQueen, int secondQueen)
{
  int x1 = firstQueen;
  int y1 = solution[firstQueen];
  int x2 = secondQueen;
  int y2 = solution[secondQueen];

  if (x1 == x2)
  {
    return true;
  }

  if (y1 == y2)
  {
    return true;
  } 

  if (x1 - y1 == x2 - y2)
  {
    return true;
  } 

  if (x1 + y1 == x2 + y2)
  {
    return true;
  }

  return false;
}

int calculateQueenConflicts(const vector<int>& solution, int queenNumber)
{
  int conflictsNumber = 0;

  for (int i = 0; i < solution.size(); ++i)
  {
    if (i == queenNumber)
    {
      continue;
    }
    if (hasConflict(solution, queenNumber, i))
    {
      ++conflictsNumber;
    }
  }
  return conflictsNumber;
}

long long calculateConflictsNumber(const vector<int>& solution)
{
  int conflictsNumber = 0;
  for (int i = 0; i < solution.size(); ++i)
  {
    conflictsNumber += calculateQueenConflicts(solution, i);
  }
  return conflictsNumber;
}

struct BoardInformation
{
  BoardInformation(int n): n(n)
  {
    queensOnRow.resize(n, 0);
    queensOnMainDiagonal.resize(2 * n + 1, 0);
    queensOnSecondDiagonal.resize(2 * n + 1, 0);
  }

  BoardInformation(const vector<int>& queens): BoardInformation(queens.size())
  {
    for (int i = 0; i < n; ++i)
    {
      addQueen(i, queens[i]);
    }
  }

  void addQueen(int x, int y)
  {
    ++queensOnRow[y];
    ++queensOnMainDiagonal[x - y + n];
    ++queensOnSecondDiagonal[x + y];
  }

  void removeQueen(int x, int y)
  {
    --queensOnRow[y];
    --queensOnMainDiagonal[x - y + n];
    --queensOnSecondDiagonal[x + y];
  }

  int positionConflictsNumber(int queenNumber, int queenPosition)
  {
    return queensOnRow[queenPosition] 
          + queensOnMainDiagonal[queenNumber - queenPosition + n]
          + queensOnSecondDiagonal[queenNumber + queenPosition];
  }

  int n;
  vector<int> queensOnRow;
  vector<int> queensOnMainDiagonal;
  vector<int> queensOnSecondDiagonal;
};

bool accept(long long delta, double temperature)
{
  return generate_canonical<double, 10>(gen) <= exp(-delta / temperature);
}

class SimulatedAnnealingSolver
{
 public:
  Solution solve(const Solution& startingSolution, double startingTemperature, double alpha)
  {
    int n = startingSolution.queens.size();
    Solution solution = startingSolution;
    BoardInformation boardInformation(solution.queens);

    solution.conflictsNumber = 0;
    for (int i = 0; i < n; ++i)
    {
      solution.conflictsNumber += boardInformation.positionConflictsNumber(i, solution.queens[i]) - 3;
    }

    Solution bestSolution = solution;

    cerr << "Conflicts number: " << solution.conflictsNumber << endl;

    double temperature = startingTemperature;
    while (temperature > 1e-6)
    {
      for (int it = 0; it < 30000; ++it)
      {
        int queen = randomInt(n);

        // if (solution.conflictsNumber < 80)
        // {
        //   for (int i = 0; i < n; ++i)
        //   {
        //     if (boardInformation.positionConflictsNumber(i, solution.queens[i]) - 3 > 0) 
        //     {
        //       queen = i;
        //     }
        //   }
        // }

        int newPosition = randomInt(n);
        int oldPosition = solution.queens[queen];

        // if (solution.conflictsNumber < 80)
        // {
        //   for (int j = 0; j < n; ++j)
        //   {
        //     if (boardInformation.positionConflictsNumber(queen, j) == 0)
        //     {
        //       newPosition = j;
        //     }
        //   }
        // }


        if (newPosition == oldPosition)
        {
          continue;
        }

        long long queenOldConflicts = (boardInformation.positionConflictsNumber(queen, oldPosition) - 3) * 2;
        long long queenNewConflicts = (boardInformation.positionConflictsNumber(queen, newPosition)) * 2;

        long long newConflictsNumber = solution.conflictsNumber - queenOldConflicts + queenNewConflicts;

        if (accept(newConflictsNumber - solution.conflictsNumber, temperature))
        {
          boardInformation.removeQueen(queen, oldPosition);
          solution.queens[queen] = newPosition;
          boardInformation.addQueen(queen, newPosition);
          solution.conflictsNumber = newConflictsNumber;
        }

        if (solution.conflictsNumber < bestSolution.conflictsNumber)
        {
          bestSolution = solution;
          if (bestSolution.conflictsNumber == 0)
          {
            return bestSolution;
          }
        }
      }

      temperature *= alpha;

      fprintf(stderr, "Conflicts: %10d | Temperature: %6.6f\n", solution.conflictsNumber, temperature);

      //assert(calculateConflictsNumber(solution.queens) == solution.conflictsNumber);
    }

    cerr << "Best conflicts number: " << bestSolution.conflictsNumber << endl;

    return bestSolution;
  }
};

class RandomSolver
{
 public:
  Solution solve(int n)
  {
    vector<int> queens;
    for (int i = 0; i < n; ++i)
    {
      queens.push_back(randomInt(n));
    }
    return Solution(n * 1ll * n, queens);
  }
};

class QueensSolver
{
 public:
  Solution solve(int n)
  {
    srand(42);
    RandomSolver randomSolver;
    SimulatedAnnealingSolver simulatedAnnealingSolver;

    Solution bestSolution = randomSolver.solve(n);

    for (int it = 0; it < n; ++it)
    {
      Solution solution = randomSolver.solve(n);
      solution = simulatedAnnealingSolver.solve(solution, 5, 0.99);

      if (solution.conflictsNumber < bestSolution.conflictsNumber)
      {
        bestSolution = solution;
      }

      if (bestSolution.conflictsNumber == 0)
      {
        break;
      }
    }
    return bestSolution;
  }
};

int main(int argc, char** argv) 
{
  if (argc != 2)
  {
    printf("Usage: queensSolver fileName");
    return 0;
  }
  int n = readInput(argv[1]);

  QueensSolver queensSolver;
  Solution solution = queensSolver.solve(n);

  if (solution.conflictsNumber == 0)
  {
    printf("%d\n", n);
    printSolution(solution);
  }
  else
  {
    printf("No solution\n");
  }

  return 0;
}
