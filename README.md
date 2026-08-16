# TargetPath Portfolio Model

TargetPath is a Java/JavaFX retirement portfolio modeling application designed to explore dynamic asset allocation, target-date glide paths, and long-term retirement outcomes.

## Application Preview

### Welcome Screen
The TargetPath dashboard introduces the retirement portfolio modeling platform and provides access to the portfolio construction, allocation, and risk-analysis tools.

![TargetPath Welcome Screen](images/welcome.png)

### Portfolio Builder — Investor Inputs
Investors can enter their age, income, current portfolio balance, retirement horizon, contribution assumptions, employer match, and other planning variables.

![TargetPath Portfolio Builder Inputs](images/portfolio-builder-inputs.png)

### Portfolio Builder — Results
TargetPath converts investor inputs into a dynamic target-date portfolio, projected retirement wealth, asset allocation, and long-term portfolio growth.

![TargetPath Portfolio Builder Results](images/portfolio-builder-results.png)

### Portfolio Construction & Investment Guidance
The model translates strategic asset-allocation targets into benchmark-based investment exposures while explaining the portfolio construction methodology.

![TargetPath Portfolio Instructions](images/instructions.png)

### Annual Capital Allocation Plan
The annual allocation model shows how stock, bond, and cash exposure changes each year as the investor approaches retirement, including projected dollar allocations.

![TargetPath Annual Allocation Plan](images/annual-plan.png)

### Risk & Scenario Analysis
TargetPath incorporates Monte Carlo simulation, expected volatility, inflation-adjusted returns, risk metrics, and conservative, base, and optimistic scenarios to illustrate the uncertainty surrounding long-term investment outcomes.

![TargetPath Risk and Scenario Analysis](images/risk-analysis.png)

## Key Features

- Dynamic target-date glide path
- Forward-looking capital market assumptions
- Year-by-year stock, bond, and cash allocation
- Benchmark-based portfolio construction
- 5,000-path Monte Carlo simulation
- 3 (Conservative, base, and optimistic) scenario analysis
- Expected volatility and Sharpe ratio analysis
- Inflation-adjusted retirement wealth
- Salary growth and employer match modeling
- Fund fee-drag analysis
- Retirement-readiness assessment

## Portfolio Framework

The model separates strategic asset allocation from specific investment products.

Examples of modeled benchmark exposures include:

- CRSP U.S. Total Market Index
- S&P 500 Index
- Bloomberg U.S. Aggregate Bond Index
- Bloomberg U.S. Treasury Index
- U.S. Treasury bills / government money-market exposure

Benchmark references are used solely to identify modeled market exposures and do not imply any endorsement or affiliations.

## Capital Market Assumptions

The base model uses forward-looking assumptions for major asset classes and dynamically calculates portfolio expected return based on the investor's glide-path allocation.

Scenario analysis includes conservative, base, and optimistic assumptions.

## Monte Carlo Analysis

The application runs 5,000 simulated retirement paths using expected return and volatility assumptions to estimate a distribution of potential retirement outcomes.

Outputs include:

- 10th percentile outcome
- Median outcome
- 90th percentile outcome
- Probability of reaching the investor's selected retirement goal

## Technology

- Java
- JavaFX
- Maven
- Object-oriented programming
- Financial modeling
- Monte Carlo simulation

## Methodology

TargetPath combines:

1. Investor age and desired retirement year
2. Annual income and contribution rate
3. Employer matching contributions
4. Expected salary growth percentage
5. Dynamic stock/bond/cash glide paths
6. Forward-looking return assumptions
7. Investment fee drag
8. Inflation
9. Risk and scenario analysis
10. Retirement-income readiness

## Author

**Luke Slattery**  
Finance

## Disclaimer

This application is an educational financial-modeling project and does not provide investment, tax, legal, accounting, or fiduciary advice. Capital market assumptions are illustrative and actual investment outcomes may differ.