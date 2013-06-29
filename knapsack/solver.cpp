#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <algorithm>
#include <functional>
#include <stdexcept>
#include <numeric>
#include <memory.h>

using std::cerr;
using std::cin;
using std::cout;
using std::endl;
using std::vector;

struct Item
{
  int value, weight;
};

void readInput(int& capacity, vector<Item>* items)
{
  int itemsNumber;
  cin >> itemsNumber >> capacity;
  items->resize(itemsNumber);
  for (int i = 0; i < itemsNumber; ++i)
  {
    int& value = (*items)[i].value;
    int& weight = (*items)[i].weight;
    cin >> value >> weight;
  }
}

void printDataStatistics(int capacity, const vector<Item>& items)
{
  cerr << "Capacity equals " << capacity << endl;
  cerr << "Total number of items: " << items.size() << endl;

  {
    int totalWeight = 0;
    int totalValue = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      totalValue += items[i].value;
      totalWeight += items[i].weight;
    }

    cerr << "Total value: " << totalValue << endl;
    cerr << "Total weight: " << totalWeight << endl;

    cerr << "Average value: " << double(totalValue) / totalWeight << endl;
    cerr << "Average weight: " << double(totalWeight) / items.size() << endl;
  }

  {
    int tooHeavyItemsNumber = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      if (items[i].weight > capacity)
      {
        ++tooHeavyItemsNumber;
      }
    }
    cerr << "Too heavy items number: " << tooHeavyItemsNumber << endl;
  }

  // Weight distribution
  {
    int rangesNumber = 9;
    int weightDensity[rangesNumber];
    memset(weightDensity, 0, sizeof(weightDensity));
    int pow10 = 1;
    for (int i = 0; i < items.size(); ++i)
    {
      int weight = items[i].weight;
      pow10 = 1;
      for (int j = 0; j < rangesNumber; ++j)
      {
        if (weight <= pow10 * 10)
        {
          ++weightDensity[j];
        }
        pow10 *= 10;
      }
    }
    cerr << "Weight distribution: " << endl;
    pow10 = 1;
    for (int i = 0; i < rangesNumber; ++i)
    {
      int itemsNumber = weightDensity[i];
      if (i > 0)
      {
        itemsNumber -= weightDensity[i - 1];
      }
      cerr << "Items ith weight in range[" << pow10 << ", " << pow10 * 10 << "]: " << itemsNumber << endl;
      pow10 *= 10;
    }
  }
}

class KnapsackSolver
{
public:
  KnapsackSolver(int capacity, const vector<Item>& items): 
    capacity(capacity), 
    items(items) 
  {
  }

  virtual ~KnapsackSolver()
  {
  }

  virtual int solve(vector<char>* taken) = 0;

protected:
  int capacity;
  vector<Item> items;
};

class DynamicProgrammingKnapsackSolver : public KnapsackSolver
{
public:
  DynamicProgrammingKnapsackSolver(int capacity, const vector<Item>& items): KnapsackSolver(capacity, items)
  {
  }

  int solve(vector<char>* taken)
  {
    
  }
};

class GreedyKnapsackSolver : public KnapsackSolver
{
public:
  GreedyKnapsackSolver(int capacity, const vector<Item>& items): KnapsackSolver(capacity, items)
  {
  }

  int solve(vector<char>* taken)
  {
    sort(items.begin(), items.end(), 
      [](const Item& lhs, const Item& rhs)
      {
        return double(lhs.value) / lhs.weight > double(rhs.value) / rhs.weight;
      }
    );
    taken->assign(items.size(), false);
    int totalValue = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      if (capacity > items[i].weight)
      {
        capacity -= items[i].weight;
        totalValue += items[i].value;
        (*taken)[i] = true;
      }
    }
    return totalValue;
  }
};

int calculateValue(int capacity, const vector<Item>& items, const vector<char>& taken)
{
  int totalWeight = 0;
  int totalValue = 0;
  for (int i = 0; i < items.size(); ++i)
  {
    if (taken[i])
    {
      totalWeight += items[i].weight;
      totalValue += items[i].value;
    }
  }
  if (totalWeight > capacity)
  {
    throw std::logic_error("totalWeight > capacity");
  }
  return totalValue;
}

void outputAnswer(int capacity, const vector<Item>& items, const vector<char>& taken)
{
  int totalValue = calculateValue(capacity, items, taken);
  cout << totalValue << " " << 0 << endl;
  for (int i = 0; i < taken.size(); ++i)
  {
    cout << int(taken[i]) << " ";
  }
  cout << endl;
}

int main(int argc, char** argv)
{
  if (argc > 2) 
  {
    cerr << "Usage: solve [inputFile]" << endl;
    return 1;
  }
  if (argc == 2)
  {
    freopen(argv[1], "r", stdin);
  }

  int capacity;
  vector<Item> items;
  readInput(capacity, &items);

  printDataStatistics(capacity, items);

  // DynamicProgrammingKnapsackSolver dpSolver(capacity, items);
  GreedyKnapsackSolver greedySolver(capacity, items);
  vector<char> taken;
  int totalValue = greedySolver.solve(&taken);
  outputAnswer(capacity, items, taken);

  return 0;
}
