/*
 * TargetPath — Retirement Portfolio Modeling & Allocation
 * Copyright © 2026 Luke Slattery. All rights reserved.
 *
 * Educational portfolio-modeling software. This application does not provide
 * investment, tax, legal, accounting, or fiduciary advice. Market benchmarks
 * are referenced solely to identify modeled investment exposures. No affiliation
 * with or endorsement by any index provider, financial institution, university,
 * or other third party is implied.
 *
 * Capital-market assumptions, expense ratios, volatility estimates, inflation,
 * withdrawal rates, and Monte Carlo outputs are illustrative modeling inputs.
 * Actual investment outcomes can differ materially.
 */

package com.example.targetdatefundappjava;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class TargetDateFundApp extends Application {

    // ============================================================
    // NAVY & GOLD VISUAL THEME
    // ============================================================

    private static final String NAVY = "#0B2742";
    private static final String GOLD = "#B8A36A";
    private static final String ACCENT_GOLD = "#D5B85A";
    private static final String BACKGROUND = "#F4F6F8";
    private static final String BORDER = "#D8DEE5";
    private static final String TEXT = "#182230";
    private static final String MUTED = "#667383";
    private static final String SUCCESS = "#176B45";
    private static final String ERROR = "#B3261E";

    // ============================================================
    // FORECAST ASSUMPTIONS
    // ============================================================

    /*
     * Midpoints of the forward-looking ranges supplied for this project:
     * U.S. stocks: 4.2% to 6.2% -> midpoint 5.2%
     * Core bonds:  3.8% to 4.8% -> midpoint 4.3%
     *
     * Cash was not included in the supplied forecast, so the app keeps
     * cash as an editable assumption with a 3.0% default.
     */
    private static final double STOCK_EXPECTED_RETURN = 0.052;
    private static final double BOND_EXPECTED_RETURN = 0.043;
    private static final double STOCK_LOW_RETURN = 0.042;
    private static final double STOCK_HIGH_RETURN = 0.062;
    private static final double BOND_LOW_RETURN = 0.038;
    private static final double BOND_HIGH_RETURN = 0.048;
    private static final int MONTE_CARLO_SIMULATIONS = 5_000;
    private static final double STOCK_BOND_CORRELATION = 0.10;

    private static final long[] INCOME_LEVELS = {
            100_000, 200_000, 300_000, 400_000, 500_000,
            600_000, 700_000, 800_000, 900_000, 1_000_000,
            2_000_000, 3_000_000, 4_000_000, 5_000_000
    };

    private static final String APP_VERSION = "1.0.0";
    private static final String PLATFORM_SPONSOR = "No Proprietary Sponsor";
    private static final double PROPRIETARY_COST_TOLERANCE = 0.0005;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    // ============================================================
    // INPUTS
    // ============================================================

    private final Slider incomeSlider = new Slider(0, INCOME_LEVELS.length - 1, 4);
    private final Label incomeDisplay = new Label();
    private final TextField ageInput = new TextField();
    private final TextField retirementYearInput = new TextField();
    private final TextField currentBalanceInput = new TextField("0");
    private final TextField contributionRateInput = new TextField("10");
    private final TextField expenseRatioCeilingInput = new TextField("0.20");
    private final TextField cashExpectedReturnInput = new TextField("3.0");
    private final TextField inflationRateInput = new TextField("2.5");
    private final TextField stockVolatilityInput = new TextField("18.0");
    private final TextField bondVolatilityInput = new TextField("6.0");
    private final TextField cashVolatilityInput = new TextField("1.0");
    private final TextField retirementGoalInput = new TextField("2000000");
    private final TextField desiredRetirementIncomeInput = new TextField("80000");
    private final TextField withdrawalRateInput = new TextField("4.0");
    private final TextField salaryGrowthInput = new TextField("2.5");
    private final TextField employerMatchRateInput = new TextField("100");
    private final TextField employerMatchCapInput = new TextField("5");
    private final ComboBox<GlidePathType> glidePathSelector = new ComboBox<>();
    private final CheckBox preferIndexFundsCheckBox = new CheckBox("Prefer benchmark-tracking implementations");
    private final Label builderErrorLabel = new Label();

    // ============================================================
    // SUMMARY OUTPUTS
    // ============================================================

    private final Label fundNameOutput = outputLabel();
    private final Label retirementAgeOutput = outputLabel();
    private final Label yearsToRetirementOutput = outputLabel();
    private final Label annualContributionOutput = outputLabel();
    private final Label monthlyContributionOutput = outputLabel();
    private final Label startingExpectedReturnOutput = outputLabel();
    private final Label retirementExpectedReturnOutput = outputLabel();
    private final Label projectedBalanceOutput = outputLabel();
    private final Label totalContributionsOutput = outputLabel();
    private final Label projectedGrowthOutput = outputLabel();
    private final Label realProjectedBalanceOutput = outputLabel();
    private final Label riskExpectedReturnOutput = outputLabel();
    private final Label riskRealBalanceOutput = outputLabel();
    private final Label currentVolatilityOutput = outputLabel();
    private final Label currentSharpeOutput = outputLabel();
    private final Label currentRealReturnOutput = outputLabel();
    private final Label monteCarloP10Output = outputLabel();
    private final Label monteCarloMedianOutput = outputLabel();
    private final Label monteCarloP90Output = outputLabel();
    private final Label goalProbabilityOutput = outputLabel();
    private final Label conservativeScenarioOutput = outputLabel();
    private final Label baseScenarioOutput = outputLabel();
    private final Label optimisticScenarioOutput = outputLabel();
    private final Label readinessStatusOutput = wrappedOutputLabel();
    private final Label requiredNestEggOutput = outputLabel();
    private final Label sustainableIncomeOutput = outputLabel();
    private final Label retirementIncomeGapOutput = outputLabel();
    private final Label readinessRatioOutput = outputLabel();
    private final Label additionalAnnualSavingsOutput = outputLabel();
    private final Label firstYearEmployeeContributionOutput = outputLabel();
    private final Label firstYearEmployerMatchOutput = outputLabel();
    private final Label retirementYearContributionOutput = outputLabel();
    private final Label grossEndingBalanceOutput = outputLabel();
    private final Label netEndingBalanceOutput = outputLabel();
    private final Label cumulativeFeeImpactOutput = outputLabel();
    private final Label feeImpactPercentOutput = outputLabel();
    private final Label riskStatusLabel = wrappedOutputLabel();
    private final Label glidePathOutput = outputLabel();

    private final Label stockOutput = outputLabel();
    private final Label bondOutput = outputLabel();
    private final Label cashOutput = outputLabel();
    private final ProgressBar stockBar = new ProgressBar(0);
    private final ProgressBar bondBar = new ProgressBar(0);
    private final ProgressBar cashBar = new ProgressBar(0);

    private final Label stockFundOutput = wrappedOutputLabel();
    private final Label bondFundOutput = wrappedOutputLabel();
    private final Label cashFundOutput = wrappedOutputLabel();
    private final Label weightedExpenseRatioOutput = outputLabel();
    private final Label estimatedAnnualCostOutput = outputLabel();
    private final Label disclosureOutput = wrappedOutputLabel();

    // ============================================================
    // CHART
    // ============================================================

    private final CategoryAxis chartXAxis = new CategoryAxis();
    private final NumberAxis chartYAxis = new NumberAxis();
    private final LineChart<String, Number> projectionChart = new LineChart<>(chartXAxis, chartYAxis);

    // ============================================================
    // ANNUAL ALLOCATION TABLE
    // ============================================================

    private final TableView<AnnualAllocationRow> allocationTable = new TableView<>();
    private final Label allocationPlanStatus = new Label(
            "Calculate a portfolio to generate your year-by-year investment allocation plan."
    );
    private final Label allocationPlanSummary = wrappedOutputLabel();

    // ============================================================
    // YOUR PORTFOLIO IMPLEMENTATION TAB
    // ============================================================

    private final ComboBox<PortfolioStyle> portfolioStyleSelector = new ComboBox<>();
    private final ComboBox<Integer> portfolioYearSelector = new ComboBox<>();
    private final TableView<PortfolioImplementationRow> portfolioImplementationTable = new TableView<>();
    private final Label portfolioImplementationStatus = new Label(
            "Calculate a portfolio to see how the stock, bond and cash targets can be implemented."
    );
    private final Label portfolioImplementationSummary = wrappedOutputLabel();

    private final TableView<PortfolioImplementationRow> annualCapitalBreakdownTable = new TableView<>();
    private final Label annualCapitalBreakdownSummary = wrappedOutputLabel();

    private Allocation latestCalculatedAllocation;
    private double latestCalculatedBalance;
    private ProjectionResult latestProjection;

    // ============================================================
    // ENGINES / DATA
    // ============================================================

    private final AllocationEngine allocationEngine = new AgeBasedAllocationEngine();
    private final List<Fund> availableFunds = createAvailableFunds();

    @Override
    public void start(Stage stage) {
        configureInputs();
        configureProjectionChart();
        configureAllocationTable();
        configurePortfolioImplementationTable();
        configureAnnualCapitalBreakdownTable();

        BorderPane root = new BorderPane();
        root.setTop(createAppHeader());

        TabPane tabPane = new TabPane();
        Tab welcomeTab = new Tab("Welcome");
        Tab builderTab = new Tab("Portfolio Builder", createBuilderTabContent());
        Tab portfolioTab = new Tab("Your Portfolio", createPortfolioImplementationTabContent());
        Tab allocationTab = new Tab("Annual Allocation Plan", createAllocationPlanTabContent());
        Tab riskTab = new Tab("Risk & Scenarios", createRiskAndScenarioTabContent());
        Tab methodologyTab = new Tab("Methodology & Assumptions", createMethodologyTabContent());

        welcomeTab.setContent(createWelcomeTabContent(tabPane, builderTab));

        welcomeTab.setClosable(false);
        builderTab.setClosable(false);
        portfolioTab.setClosable(false);
        allocationTab.setClosable(false);
        riskTab.setClosable(false);
        methodologyTab.setClosable(false);
        tabPane.getTabs().addAll(welcomeTab, builderTab, portfolioTab, allocationTab, riskTab, methodologyTab);
        tabPane.getSelectionModel().select(welcomeTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1450, 900);
        applyTheme(scene, tabPane);

        stage.setTitle("TargetPath | Retirement Portfolio Planner | v" + APP_VERSION);
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.show();
        stage.setMaximized(true);

        updateIncomeDisplay();
        loadEmptyChart();
    }

    // ============================================================
    // WELCOME / EDUCATION TAB
    // ============================================================

    private ScrollPane createWelcomeTabContent(TabPane tabPane, Tab builderTab) {
        Label eyebrow = new Label("RETIREMENT PORTFOLIO INTELLIGENCE");
        eyebrow.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;"
        );

        Label heroTitle = new Label("Plan with clarity. Invest with purpose.");
        heroTitle.setWrapText(true);
        heroTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: 900;"
        );

        Label heroText = new Label(
                "Build a personalized retirement portfolio using a dynamic glide path, forward-looking capital-market " +
                        "assumptions, risk analysis and year-by-year capital allocation."
        );
        heroText.setWrapText(true);
        heroText.setMaxWidth(850);
        heroText.setStyle("-fx-text-fill: #E8EEF2; -fx-font-size: 14px; -fx-line-spacing: 3px;");

        Button beginButton = new Button("CREATE MY RETIREMENT PLAN  →");
        beginButton.setPrefHeight(44);
        beginButton.setStyle(
                "-fx-background-color: " + GOLD + ";" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-background-radius: 7px;" +
                        "-fx-padding: 0 22px 0 22px;" +
                        "-fx-cursor: hand;"
        );
        beginButton.setOnAction(event -> tabPane.getSelectionModel().select(builderTab));

        VBox hero = new VBox(11, eyebrow, heroTitle, heroText, beginButton);
        hero.setPadding(new Insets(32));
        hero.setStyle(
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-background-radius: 16px;"
        );

        HBox featureCards = new HBox(
                14,
                createWelcomeFeatureCard(
                        "01", "PERSONALIZED GLIDE PATH",
                        "The model changes the stock, bond and cash mix as retirement approaches instead of using one allocation forever."
                ),
                createWelcomeFeatureCard(
                        "02", "FORWARD-LOOKING RETURNS",
                        "Portfolio growth uses the model's stock, bond and cash return assumptions and recalculates the expected return as allocation changes."
                ),
                createWelcomeFeatureCard(
                        "03", "PORTFOLIO IMPLEMENTATION",
                        "The Your Portfolio tab translates stocks, bonds and cash into broad investment categories using Simple, Diversified or Advanced portfolio styles."
                ),
                createWelcomeFeatureCard(
                        "04", "YEAR-BY-YEAR PLAN",
                        "The Annual Allocation Plan shows target percentages and projected dollar amounts for every year through retirement."
                )
        );
        for (javafx.scene.Node node : featureCards.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        Label definitionsTitle = pageTitle("Important Terms to Know");
        Label definitionsIntro = pageDescription(
                "These concepts drive the calculations in the planner. Understanding them makes the retirement projection and annual allocation plan easier to interpret."
        );

        GridPane definitions = new GridPane();
        definitions.setHgap(14);
        definitions.setVgap(14);
        definitions.getColumnConstraints().addAll(flexibleColumn(), flexibleColumn());

        definitions.add(createDefinitionCard(
                "Glide Path",
                "The schedule that gradually changes a portfolio's asset allocation as the investor gets closer to retirement. " +
                        "A target-date strategy typically reduces stock exposure and increases bonds and cash over time."
        ), 0, 0);

        definitions.add(createDefinitionCard(
                "Asset Allocation",
                "The percentage of the portfolio invested in major asset classes such as stocks, bonds and cash. " +
                        "The allocation determines how much investment risk and growth potential the portfolio carries."
        ), 1, 0);

        definitions.add(createDefinitionCard(
                "Expected Return",
                "A planning assumption for the average annual investment return of an asset class or portfolio. " +
                        "It is an estimate, not a guaranteed return, and actual market results can be higher or lower."
        ), 0, 1);

        definitions.add(createDefinitionCard(
                "Expense Ratio",
                "The annual operating cost of a fund expressed as a percentage of assets. A 0.20% expense ratio costs about $20 per year for every $10,000 invested, before changes in account value."
        ), 1, 1);

        definitions.add(createDefinitionCard(
                "Index Fund",
                "A fund designed to track a market index rather than rely primarily on active security selection. " +
                        "Index funds often have lower expenses, although cost and investment exposure vary by fund."
        ), 0, 2);

        definitions.add(createDefinitionCard(
                "Contribution Rate",
                "The percentage of yearly income the model assumes is contributed to the retirement portfolio. " +
                        "For example, a 10% rate on $100,000 of income represents $10,000 of annual contributions."
        ), 1, 2);

        definitions.add(createDefinitionCard(
                "Projected Balance",
                "The model's estimated portfolio value at a future date after applying contributions and modeled investment returns. " +
                        "It is a planning estimate and should not be interpreted as a guaranteed retirement balance."
        ), 0, 3);

        definitions.add(createDefinitionCard(
                "Rebalancing",
                "Adjusting investments back toward the target asset-allocation percentages. The Annual Allocation Plan provides the target mix for each year, while actual market movements may cause the portfolio to drift away from those targets."
        ), 1, 3);

        definitions.add(createDefinitionCard(
                "Portfolio Style",
                "The level of detail used to implement the target allocation. Simple uses a small number of broad market categories, Diversified adds more international and bond diversification, and Advanced separates the portfolio into additional market segments."
        ), 0, 4);

        definitions.add(createDefinitionCard(
                "Total Market Index",
                "A broad investment category designed to represent a large portion of a market rather than a small group of individual securities. It can provide wide diversification within a single fund category."
        ), 1, 4);

        definitions.add(createDefinitionCard(
                "International Developed Markets",
                "Stocks from established economies outside the United States, such as many countries in Europe and the Asia-Pacific region. This exposure can diversify a U.S.-focused stock portfolio."
        ), 0, 5);

        definitions.add(createDefinitionCard(
                "Emerging Markets",
                "Stocks from developing economies that may offer higher growth potential but can also experience greater market, currency and political risk."
        ), 1, 5);

        definitions.add(createDefinitionCard(
                "TIPS",
                "U.S. Treasury Inflation-Protected Securities. Their principal value adjusts with inflation, which can make them useful as part of the bond allocation for investors concerned about purchasing power."
        ), 0, 6);

        VBox howItWorks = new VBox(
                10,
                cardHeading("How the Planner Works"),
                createStepRow("1", "Enter your information", "Provide age, income, current savings, contribution rate and desired retirement year."),
                createStepRow("2", "Build the target portfolio", "The glide-path engine determines the current stock, bond and cash allocation."),
                createStepRow("3", "Choose how to implement it", "Use the Your Portfolio tab to translate the stock, bond and cash targets into broad investment categories."),
                createStepRow("4", "Project retirement growth", "Expected return is recalculated as the portfolio allocation changes over time."),
                createStepRow("5", "Follow the annual plan", "Review the target allocation for each year and use the percentages as a rebalancing guide.")
        );
        styleCard(howItWorks);

        Label creatorCredit = new Label("Independent project by Luke Slattery • Finance");
        creatorCredit.setWrapText(true);
        creatorCredit.setAlignment(Pos.CENTER);
        creatorCredit.setMaxWidth(Double.MAX_VALUE);
        creatorCredit.setStyle(
                "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-padding: 8px 0 0 0;"
        );

        Label educationNote = new Label(
                "Educational planning tool only. Projections are based on assumptions and do not predict or guarantee future investment performance. " +
                        "Taxes, inflation, employer-plan rules, contribution limits and individual investment circumstances may affect actual outcomes."
        );
        educationNote.setWrapText(true);
        educationNote.setStyle(
                "-fx-background-color: #F0EDE3;" +
                        "-fx-background-radius: 9px;" +
                        "-fx-padding: 14px;" +
                        "-fx-text-fill: " + MUTED + ";" +
                        "-fx-font-size: 10.5px;"
        );

        VBox content = new VBox(
                18,
                hero,
                featureCards,
                definitionsTitle,
                definitionsIntro,
                definitions,
                howItWorks,
                educationNote,
                creatorCredit
        );
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    private VBox createWelcomeFeatureCard(String number, String title, String description) {
        Label numberLabel = new Label(number);
        numberLabel.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 900;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;"
        );

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px; -fx-line-spacing: 2px;");

        VBox card = new VBox(8, numberLabel, titleLabel, descriptionLabel);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(150);
        styleCard(card);
        return card;
    }

    private VBox createDefinitionCard(String term, String definition) {
        Label termLabel = new Label(term);
        termLabel.setStyle(
                "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 900;"
        );

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(55);
        accent.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 2px;");

        Label definitionLabel = new Label(definition);
        definitionLabel.setWrapText(true);
        definitionLabel.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px; -fx-line-spacing: 2px;");

        VBox card = new VBox(7, termLabel, accent, definitionLabel);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(135);
        styleCard(card);
        GridPane.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox createStepRow(String number, String title, String description) {
        Label badge = new Label(number);
        badge.setMinSize(30, 30);
        badge.setAlignment(Pos.CENTER);
        badge.setStyle(
                "-fx-background-color: " + GOLD + ";" +
                        "-fx-background-radius: 15px;" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-weight: 900;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-weight: bold; -fx-font-size: 11.5px;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        VBox text = new VBox(2, titleLabel, descriptionLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        HBox row = new HBox(11, badge, text);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        return row;
    }

    // ============================================================
    // APP HEADER
    // ============================================================

    private HBox createAppHeader() {
        Label mark = new Label("TP");
        mark.setMinSize(46, 46);
        mark.setAlignment(Pos.CENTER);
        mark.setStyle(
                "-fx-background-color: " + GOLD + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 900;"
        );

        Label title = new Label("TARGETPATH");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 19px;" +
                        "-fx-font-weight: 900;"
        );

        Label subtitle = new Label("Retirement Portfolio Modeling & Allocation");
        subtitle.setStyle("-fx-text-fill: #CBD6E0; -fx-font-size: 11px;");

        VBox text = new VBox(1, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label descriptor = new Label("FORWARD-LOOKING  •  RISK-AWARE  •  GOAL-BASED");
        descriptor.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: 800;"
        );

        HBox header = new HBox(14, mark, text, spacer, descriptor);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle(
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-border-color: transparent transparent " + GOLD + " transparent;" +
                        "-fx-border-width: 0 0 3px 0;"
        );
        return header;
    }

    // ============================================================
    // BUILDER TAB
    // ============================================================

    private ScrollPane createBuilderTabContent() {
        Label title = pageTitle("Build Your Retirement Portfolio");
        Label description = pageDescription(
                "Enter your retirement assumptions. The model automatically changes expected return each year " +
                        "as the portfolio moves through its target-date glide path."
        );

        VBox inputCard = createBuilderInputCard();
        VBox resultsCard = createResultsCard();
        VBox chartCard = createChartCard();

        HBox lower = new HBox(18, resultsCard, chartCard);
        lower.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(resultsCard, Priority.ALWAYS);
        HBox.setHgrow(chartCard, Priority.ALWAYS);
        resultsCard.setMinWidth(520);
        chartCard.setMinWidth(560);

        VBox content = new VBox(16, title, description, inputCard, lower);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    private VBox createBuilderInputCard() {
        VBox incomeSection = new VBox(
                7,
                sectionLabel("YEARLY INCOME"),
                incomeDisplay,
                incomeSlider,
                createIncomeScale()
        );

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.getColumnConstraints().addAll(flexibleColumn(), flexibleColumn(), flexibleColumn());

        grid.add(inputSection("Current Age", ageInput, "Example: 25"), 0, 0);
        grid.add(inputSection("Desired Retirement Year", retirementYearInput,
                "Example: " + (Year.now().getValue() + 40)), 1, 0);
        grid.add(inputSection("Current Portfolio Balance", currentBalanceInput, "Example: 50000"), 2, 0);
        grid.add(inputSection("Contribution Rate (%)", contributionRateInput, "Example: 10"), 0, 1);
        grid.add(inputSection("Maximum Implementation Expense (%)", expenseRatioCeilingInput, "Example: 0.20"), 1, 1);
        grid.add(inputSection("Cash Expected Return (%)", cashExpectedReturnInput, "Default: 3.0"), 2, 1);
        grid.add(inputSection("Inflation Rate (%)", inflationRateInput, "Default: 2.5"), 0, 2);
        grid.add(inputSection("Retirement Goal ($)", retirementGoalInput, "Example: 2000000"), 1, 2);
        grid.add(inputSection("Desired Annual Retirement Income ($)", desiredRetirementIncomeInput, "Example: 80000"), 2, 2);
        grid.add(inputSection("Annual Salary Growth (%)", salaryGrowthInput, "Default: 2.5"), 0, 3);
        grid.add(inputSection("Employer Match Rate (%)", employerMatchRateInput, "Example: 100"), 1, 3);
        grid.add(inputSection("Employer Match Cap (% of Salary)", employerMatchCapInput, "Example: 5"), 2, 3);
        grid.add(inputSection("Planning Withdrawal Rate (%)", withdrawalRateInput, "Default: 4.0"), 0, 4);

        VBox riskAssumptions = new VBox(6,
                sectionLabel("RISK ASSUMPTIONS (ADVANCED)"),
                new HBox(10,
                        compactInput("Stock Volatility %", stockVolatilityInput),
                        compactInput("Bond Volatility %", bondVolatilityInput),
                        compactInput("Cash Volatility %", cashVolatilityInput)
                )
        );

        glidePathSelector.setMaxWidth(Double.MAX_VALUE);
        VBox glideSection = new VBox(6, sectionLabel("GLIDE PATH"), glidePathSelector);

        Label returnNote = new Label(
                "Model return assumptions: U.S. stocks 5.2% and core bonds 4.3%, using the midpoints " +
                        "of the supplied 10-year forecast ranges. Cash is editable because no cash forecast was supplied."
        );
        returnNote.setWrapText(true);
        returnNote.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        Label glideHelp = new Label(
                "To retirement reaches its final allocation at retirement. Through retirement remains somewhat " +
                        "more growth-oriented at the retirement date."
        );
        glideHelp.setWrapText(true);
        glideHelp.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        Button calculate = new Button("CALCULATE PORTFOLIO & ANNUAL PLAN");
        calculate.setMaxWidth(Double.MAX_VALUE);
        calculate.setPrefHeight(42);
        stylePrimaryButton(calculate);
        calculate.setOnAction(e -> calculateModelPortfolio());

        builderErrorLabel.setWrapText(true);

        VBox card = new VBox(
                14,
                cardHeading("Investor Inputs"),
                incomeSection,
                grid,
                glideSection,
                glideHelp,
                preferIndexFundsCheckBox,
                riskAssumptions,
                returnNote,
                calculate,
                builderErrorLabel
        );
        styleCard(card);
        return card;
    }

    private VBox createResultsCard() {
        GridPane summary = resultGrid();
        addResultRow(summary, 0, "Suggested Portfolio", fundNameOutput);
        addResultRow(summary, 1, "Years Until Retirement", yearsToRetirementOutput);
        addResultRow(summary, 2, "Projected Retirement Age", retirementAgeOutput);
        addResultRow(summary, 3, "Annual Contribution", annualContributionOutput);
        addResultRow(summary, 4, "Monthly Contribution", monthlyContributionOutput);
        addResultRow(summary, 5, "Starting Model Return", startingExpectedReturnOutput);
        addResultRow(summary, 6, "Retirement-Year Model Return", retirementExpectedReturnOutput);
        addResultRow(summary, 7, "Projected Retirement Balance", projectedBalanceOutput);
        addResultRow(summary, 8, "Total Principal Contributed", totalContributionsOutput);
        addResultRow(summary, 9, "Projected Investment Growth", projectedGrowthOutput);
        addResultRow(summary, 10, "Inflation-Adjusted Balance", realProjectedBalanceOutput);
        addResultRow(summary, 11, "First-Year Employee Contribution", firstYearEmployeeContributionOutput);
        addResultRow(summary, 12, "First-Year Employer Match", firstYearEmployerMatchOutput);
        addResultRow(summary, 13, "Retirement-Year Total Contribution", retirementYearContributionOutput);
        addResultRow(summary, 14, "Glide Path", glidePathOutput);

        VBox allocation = new VBox(
                10,
                allocationRow("Stocks", stockBar, stockOutput),
                allocationRow("Bonds", bondBar, bondOutput),
                allocationRow("Cash", cashBar, cashOutput)
        );

        GridPane funds = resultGrid();
        addResultRow(funds, 0, "Stock Fund", stockFundOutput);
        addResultRow(funds, 1, "Bond Fund", bondFundOutput);
        addResultRow(funds, 2, "Cash Fund", cashFundOutput);
        addResultRow(funds, 3, "Weighted Expense Ratio", weightedExpenseRatioOutput);
        addResultRow(funds, 4, "Estimated Annual Fund Cost", estimatedAnnualCostOutput);

        Label disclaimer = new Label(
                "Educational projection only. Expected returns are assumptions, not guarantees. Actual returns can " +
                        "differ materially or be negative. Taxes, contribution limits, withdrawals, plan " +
                        "restrictions, taxes and advisory fees are not included. Selected fund expense ratios are included."
        );
        disclaimer.setWrapText(true);
        disclaimer.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10px;");

        VBox card = new VBox(
                13,
                cardHeading("Portfolio Results"),
                summary,
                divider(),
                cardHeading("Current Target Allocation"),
                allocation,
                divider(),
                cardHeading("Target Exposures & Benchmarks"),
                funds,
                disclosureOutput,
                divider(),
                disclaimer
        );
        styleCard(card);
        return card;
    }

    private VBox createChartCard() {
        Label description = new Label(
                "The projection recalculates allocation and expected return each year. Contributions are assumed " +
                        "to arrive evenly through each year."
        );
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        VBox card = new VBox(10, cardHeading("Projected Portfolio Growth"), description, projectionChart);
        VBox.setVgrow(projectionChart, Priority.ALWAYS);
        styleCard(card);
        return card;
    }

    // ============================================================
    // YOUR PORTFOLIO TAB
    // ============================================================

    private ScrollPane createPortfolioImplementationTabContent() {
        Label title = pageTitle("Your Recommended Portfolio");
        Label description = pageDescription(
                "The target-date engine determines how much belongs in stocks, bonds and cash for every year in the plan. " +
                        "Choose a year below to translate that exact Annual Allocation Plan target into broad investment categories " +
                        "and projected dollar amounts. It does not select individual stocks."
        );

        portfolioImplementationStatus.setWrapText(true);
        portfolioImplementationStatus.setStyle(
                "-fx-background-color: #EEF2F5;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 12px;" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-weight: bold;"
        );

        Label yearLabel = sectionLabel("PLAN YEAR");
        portfolioYearSelector.setMaxWidth(240);
        Label yearHelp = new Label(
                "Choose any year in the retirement plan to see the exact projected capital allocation for that year."
        );
        yearHelp.setWrapText(true);
        yearHelp.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        Label styleLabel = sectionLabel("PORTFOLIO STYLE");
        portfolioStyleSelector.setMaxWidth(360);
        Label styleHelp = new Label(
                "Simple keeps the implementation broad. Diversified adds more market segments. Advanced provides the most detailed breakdown."
        );
        styleHelp.setWrapText(true);
        styleHelp.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        HBox selectorRow = new HBox(24,
                new VBox(7, yearLabel, portfolioYearSelector, yearHelp),
                new VBox(7, styleLabel, portfolioStyleSelector, styleHelp)
        );
        selectorRow.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(selectorRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(selectorRow.getChildren().get(1), Priority.ALWAYS);

        VBox selectorCard = new VBox(8, selectorRow);
        styleCard(selectorCard);

        portfolioImplementationSummary.setText(
                "After calculating a portfolio, choose a plan year to see that year's stock, bond and cash targets translated into specific investment categories and projected dollar amounts."
        );
        portfolioImplementationSummary.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px;");

        VBox implementationCard = new VBox(
                12,
                cardHeading("Selected-Year Capital Allocation Breakdown"),
                portfolioImplementationSummary,
                portfolioImplementationTable
        );
        VBox.setVgrow(portfolioImplementationTable, Priority.ALWAYS);
        implementationCard.setMinHeight(500);
        styleCard(implementationCard);

        VBox explanationCard = new VBox(
                9,
                cardHeading("How to Use This Recommendation"),
                explanatoryLabel(
                        "The percentages in this table are percentages of the entire portfolio. The model first determines the total " +
                                "stock, bond and cash targets, then divides each asset-class bucket among broad investment categories."
                ),
                explanatoryLabel(
                        "You generally do not need to replace every investment every year. The same broad categories can remain in the " +
                                "portfolio while their target weights change as the glide path becomes more conservative."
                ),
                explanatoryLabel(
                        "Specific mutual funds and ETFs can differ in fees, benchmarks, tax treatment, trading restrictions and holdings. " +
                                "The categories shown here are educational implementation examples rather than personalized securities recommendations."
                )
        );
        styleCard(explanationCard);

        VBox content = new VBox(
                16,
                title,
                description,
                portfolioImplementationStatus,
                selectorCard,
                implementationCard,
                explanationCard
        );
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    private Label explanatoryLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px; -fx-line-spacing: 2px;");
        return label;
    }

    private void configurePortfolioImplementationTable() {
        portfolioImplementationTable.setPlaceholder(new Label("Calculate a portfolio to generate the investment-category breakdown."));
        portfolioImplementationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        portfolioImplementationTable.setPrefHeight(430);

        TableColumn<PortfolioImplementationRow, String> category = new TableColumn<>("Investment Category");
        category.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCategory()));
        category.setPrefWidth(240);

        TableColumn<PortfolioImplementationRow, String> assetClass = new TableColumn<>("Asset Class");
        assetClass.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getAssetClass()));
        assetClass.setPrefWidth(100);

        TableColumn<PortfolioImplementationRow, String> percentage = new TableColumn<>("Portfolio %");
        percentage.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                String.format(Locale.US, "%.2f%%", d.getValue().getPortfolioPercentage())
        ));
        percentage.setPrefWidth(100);

        TableColumn<PortfolioImplementationRow, Number> dollars = new TableColumn<>("Current $ Target");
        dollars.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getDollarTarget()));
        dollars.setCellFactory(c -> portfolioCurrencyCell());
        dollars.setPrefWidth(140);

        TableColumn<PortfolioImplementationRow, String> purpose = new TableColumn<>("Role in Portfolio");
        purpose.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getPurpose()));
        purpose.setPrefWidth(420);

        portfolioImplementationTable.getColumns().addAll(category, assetClass, percentage, dollars, purpose);
    }

    private TableCell<PortfolioImplementationRow, Number> portfolioCurrencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : currencyFormatter.format(value.doubleValue()));
            }
        };
    }

    private void updatePortfolioImplementation(Allocation allocation, double currentBalance) {
        latestCalculatedAllocation = allocation;
        latestCalculatedBalance = currentBalance;

        if (latestProjection != null && !latestProjection.getAnnualRows().isEmpty()) {
            List<Integer> years = latestProjection.getAnnualRows().stream()
                    .map(AnnualAllocationRow::getYear)
                    .collect(Collectors.toList());
            portfolioYearSelector.setItems(FXCollections.observableArrayList(years));
            if (portfolioYearSelector.getValue() == null || !years.contains(portfolioYearSelector.getValue())) {
                portfolioYearSelector.setValue(years.get(0));
            }
            updatePortfolioImplementationForYear(portfolioYearSelector.getValue());
            return;
        }

        PortfolioStyle style = portfolioStyleSelector.getValue();
        if (style == null) style = PortfolioStyle.DIVERSIFIED;
        List<PortfolioImplementationRow> rows = buildPortfolioImplementationRows(allocation, currentBalance, style);
        portfolioImplementationTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void updatePortfolioImplementationForYear(Integer selectedYear) {
        if (latestProjection == null || selectedYear == null) return;

        AnnualAllocationRow annualRow = findAnnualRow(selectedYear);
        if (annualRow == null) return;

        PortfolioStyle style = portfolioStyleSelector.getValue();
        if (style == null) style = PortfolioStyle.DIVERSIFIED;

        Allocation allocation = new Allocation(
                annualRow.getStocks(),
                annualRow.getBonds(),
                annualRow.getCash()
        );

        List<PortfolioImplementationRow> rows = buildPortfolioImplementationRows(
                allocation,
                annualRow.getProjectedBalance(),
                style
        );
        portfolioImplementationTable.setItems(FXCollections.observableArrayList(rows));

        portfolioImplementationSummary.setText(
                selectedYear + " • Age " + annualRow.getAge() + " • Projected portfolio " +
                        currencyFormatter.format(annualRow.getProjectedBalance()) + " • " +
                        style.getDisplayName() + " implementation: " +
                        annualRow.getStocks() + "% stocks / " + annualRow.getBonds() + "% bonds / " +
                        annualRow.getCash() + "% cash. The category percentages below add to 100% of the portfolio."
        );

        portfolioImplementationStatus.setText(
                "Showing the capital allocation for " + selectedYear + ". Change the Plan Year or Portfolio Style to view another point in the glide path."
        );
        portfolioImplementationStatus.setStyle(
                "-fx-background-color: #E8F4EE; -fx-background-radius: 8px; -fx-padding: 12px;" +
                        "-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;"
        );
    }

    private AnnualAllocationRow findAnnualRow(int year) {
        if (latestProjection == null) return null;
        return latestProjection.getAnnualRows().stream()
                .filter(row -> row.getYear() == year)
                .findFirst()
                .orElse(null);
    }

    private List<PortfolioImplementationRow> buildPortfolioImplementationRows(
            Allocation allocation,
            double currentBalance,
            PortfolioStyle style
    ) {
        List<PortfolioImplementationRow> rows = new ArrayList<>();

        if (style == PortfolioStyle.SIMPLE) {
            addImplementationRow(rows, "U.S. Total Stock Market", "Stocks", allocation.getStocks() * 0.60,
                    currentBalance, "Broad exposure to U.S. companies across market sizes.");
            addImplementationRow(rows, "Total International Stock Market", "Stocks", allocation.getStocks() * 0.40,
                    currentBalance, "Diversifies stock exposure outside the United States.");
            addImplementationRow(rows, "U.S. Aggregate Bond Market", "Bonds", allocation.getBonds(),
                    currentBalance, "Broad core bond exposure across government and investment-grade debt.");
            addImplementationRow(rows, "Government Money Market / Short-Term Treasury", "Cash", allocation.getCash(),
                    currentBalance, "Liquidity and capital stability within the cash allocation.");
        } else if (style == PortfolioStyle.DIVERSIFIED) {
            addImplementationRow(rows, "U.S. Total Stock Market", "Stocks", allocation.getStocks() * 0.65,
                    currentBalance, "Core U.S. equity exposure across large, mid and small companies.");
            addImplementationRow(rows, "International Developed Markets", "Stocks", allocation.getStocks() * 0.25,
                    currentBalance, "Diversification across established non-U.S. economies.");
            addImplementationRow(rows, "Emerging Markets", "Stocks", allocation.getStocks() * 0.10,
                    currentBalance, "Adds developing-economy exposure with higher potential volatility.");
            addImplementationRow(rows, "U.S. Aggregate Bond Market", "Bonds", allocation.getBonds() * 0.75,
                    currentBalance, "Primary core bond allocation.");
            addImplementationRow(rows, "Treasury Inflation-Protected Securities (TIPS)", "Bonds", allocation.getBonds() * 0.25,
                    currentBalance, "Adds inflation-sensitive government bond exposure.");
            addImplementationRow(rows, "Government Money Market / Short-Term Treasury", "Cash", allocation.getCash(),
                    currentBalance, "Liquidity and capital stability within the cash allocation.");
        } else {
            addImplementationRow(rows, "U.S. Large-Cap Stock Market", "Stocks", allocation.getStocks() * 0.40,
                    currentBalance, "Core exposure to large U.S. companies.");
            addImplementationRow(rows, "U.S. Mid-Cap Stock Market", "Stocks", allocation.getStocks() * 0.10,
                    currentBalance, "Adds exposure to medium-sized U.S. companies.");
            addImplementationRow(rows, "U.S. Small-Cap Stock Market", "Stocks", allocation.getStocks() * 0.10,
                    currentBalance, "Adds exposure to smaller U.S. companies and a different return profile.");
            addImplementationRow(rows, "International Developed Markets", "Stocks", allocation.getStocks() * 0.25,
                    currentBalance, "Diversifies across established non-U.S. economies.");
            addImplementationRow(rows, "Emerging Markets", "Stocks", allocation.getStocks() * 0.15,
                    currentBalance, "Adds developing-economy exposure with higher potential volatility.");
            addImplementationRow(rows, "U.S. Aggregate Bond Market", "Bonds", allocation.getBonds() * 0.60,
                    currentBalance, "Broad core bond exposure.");
            addImplementationRow(rows, "U.S. Treasury / Government Bonds", "Bonds", allocation.getBonds() * 0.20,
                    currentBalance, "Adds high-quality government bond exposure.");
            addImplementationRow(rows, "Treasury Inflation-Protected Securities (TIPS)", "Bonds", allocation.getBonds() * 0.20,
                    currentBalance, "Adds inflation-sensitive government bond exposure.");
            addImplementationRow(rows, "Government Money Market / Short-Term Treasury", "Cash", allocation.getCash(),
                    currentBalance, "Liquidity and capital stability within the cash allocation.");
        }
        return rows;
    }

    private void addImplementationRow(
            List<PortfolioImplementationRow> rows,
            String category,
            String assetClass,
            double percentage,
            double currentBalance,
            String purpose
    ) {
        rows.add(new PortfolioImplementationRow(
                category,
                assetClass,
                percentage,
                currentBalance * percentage / 100.0,
                purpose
        ));
    }

    // ============================================================
    // ANNUAL ALLOCATION PLAN TAB
    // ============================================================

    private ScrollPane createAllocationPlanTabContent() {
        Label title = pageTitle("Annual Investment Allocation Plan");
        Label description = pageDescription(
                "This schedule tells the investor how the portfolio should be allocated every year through the " +
                        "selected retirement date. Select any year to see both the asset-class targets and the detailed " +
                        "capital allocation across the investment categories used in Your Portfolio."
        );

        allocationPlanStatus.setWrapText(true);
        allocationPlanStatus.setStyle(
                "-fx-background-color: #EEF2F5;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 12px;" +
                        "-fx-text-fill: " + NAVY + ";" +
                        "-fx-font-weight: bold;"
        );

        allocationPlanSummary.setText(
                "After calculating a portfolio, this section will show the target stock, bond and cash percentages " +
                        "and dollar amounts for every year."
        );
        allocationPlanSummary.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px;");

        VBox tableCard = new VBox(
                12,
                cardHeading("Year-by-Year Allocation Schedule"),
                allocationPlanSummary,
                allocationTable
        );
        VBox.setVgrow(allocationTable, Priority.ALWAYS);
        styleCard(tableCard);
        tableCard.setMinHeight(610);

        annualCapitalBreakdownSummary.setText(
                "Select a year in the schedule above to see the category-level capital allocation for that exact year."
        );
        annualCapitalBreakdownSummary.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 11px;");

        VBox detailedCapitalCard = new VBox(
                12,
                cardHeading("Selected-Year Detailed Capital Allocation"),
                annualCapitalBreakdownSummary,
                annualCapitalBreakdownTable
        );
        VBox.setVgrow(annualCapitalBreakdownTable, Priority.ALWAYS);
        detailedCapitalCard.setMinHeight(430);
        styleCard(detailedCapitalCard);

        Label note = new Label(
                "How to use the plan: each year, compare the portfolio to the target percentages shown for that year. " +
                        "The dollar amounts are projected targets, not required trades. Market performance will cause actual " +
                        "balances to differ, so percentage targets are the primary rebalancing guide."
        );
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        VBox content = new VBox(16, title, description, allocationPlanStatus, tableCard, detailedCapitalCard, note);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    private void configureAllocationTable() {
        allocationTable.setPlaceholder(new Label("Calculate a portfolio to generate the annual schedule."));
        allocationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        allocationTable.setPrefHeight(560);

        TableColumn<AnnualAllocationRow, Number> year = new TableColumn<>("Year");
        year.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().getYear()));

        TableColumn<AnnualAllocationRow, Number> age = new TableColumn<>("Age");
        age.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().getAge()));

        TableColumn<AnnualAllocationRow, Number> remaining = new TableColumn<>("Years Left");
        remaining.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().getYearsToRetirement()));

        TableColumn<AnnualAllocationRow, String> stocks = new TableColumn<>("Stocks");
        stocks.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getStocks() + "%"));

        TableColumn<AnnualAllocationRow, String> bonds = new TableColumn<>("Bonds");
        bonds.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getBonds() + "%"));

        TableColumn<AnnualAllocationRow, String> cash = new TableColumn<>("Cash");
        cash.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCash() + "%"));

        TableColumn<AnnualAllocationRow, String> modelReturn = new TableColumn<>("Model Return");
        modelReturn.setCellValueFactory(d -> new ReadOnlyStringWrapper(formatPercentage(d.getValue().getExpectedReturn())));

        TableColumn<AnnualAllocationRow, Number> projectedBalance = new TableColumn<>("Projected Balance");
        projectedBalance.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getProjectedBalance()));
        projectedBalance.setCellFactory(c -> currencyCell());

        TableColumn<AnnualAllocationRow, Number> annualContribution = new TableColumn<>("Total Contribution");
        annualContribution.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getAnnualContribution()));
        annualContribution.setCellFactory(c -> currencyCell());

        TableColumn<AnnualAllocationRow, Number> stockDollars = new TableColumn<>("Stock $ Target");
        stockDollars.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getStockDollars()));
        stockDollars.setCellFactory(c -> currencyCell());

        TableColumn<AnnualAllocationRow, Number> bondDollars = new TableColumn<>("Bond $ Target");
        bondDollars.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getBondDollars()));
        bondDollars.setCellFactory(c -> currencyCell());

        TableColumn<AnnualAllocationRow, Number> cashDollars = new TableColumn<>("Cash $ Target");
        cashDollars.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getCashDollars()));
        cashDollars.setCellFactory(c -> currencyCell());

        allocationTable.getColumns().addAll(
                year, age, remaining, stocks, bonds, cash, modelReturn,
                projectedBalance, annualContribution, stockDollars, bondDollars, cashDollars
        );

        allocationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                updateAnnualCapitalBreakdown(newRow);
                if (!Integer.valueOf(newRow.getYear()).equals(portfolioYearSelector.getValue())) {
                    portfolioYearSelector.setValue(newRow.getYear());
                }
            }
        });
    }

    private void configureAnnualCapitalBreakdownTable() {
        annualCapitalBreakdownTable.setPlaceholder(new Label("Select a year in the annual schedule to see the detailed capital allocation."));
        annualCapitalBreakdownTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        annualCapitalBreakdownTable.setPrefHeight(340);

        TableColumn<PortfolioImplementationRow, String> category = new TableColumn<>("Investment Category");
        category.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCategory()));
        category.setPrefWidth(240);

        TableColumn<PortfolioImplementationRow, String> assetClass = new TableColumn<>("Asset Class");
        assetClass.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getAssetClass()));
        assetClass.setPrefWidth(100);

        TableColumn<PortfolioImplementationRow, String> percentage = new TableColumn<>("Portfolio %");
        percentage.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                String.format(Locale.US, "%.2f%%", d.getValue().getPortfolioPercentage())
        ));
        percentage.setPrefWidth(100);

        TableColumn<PortfolioImplementationRow, Number> dollars = new TableColumn<>("Projected $ Target");
        dollars.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getDollarTarget()));
        dollars.setCellFactory(c -> portfolioCurrencyCell());
        dollars.setPrefWidth(150);

        TableColumn<PortfolioImplementationRow, String> purpose = new TableColumn<>("Role in Portfolio");
        purpose.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getPurpose()));
        purpose.setPrefWidth(420);

        annualCapitalBreakdownTable.getColumns().addAll(category, assetClass, percentage, dollars, purpose);
    }

    private void updateAnnualCapitalBreakdown(AnnualAllocationRow annualRow) {
        if (annualRow == null) return;

        PortfolioStyle style = portfolioStyleSelector.getValue();
        if (style == null) style = PortfolioStyle.DIVERSIFIED;

        Allocation allocation = new Allocation(
                annualRow.getStocks(),
                annualRow.getBonds(),
                annualRow.getCash()
        );

        List<PortfolioImplementationRow> rows = buildPortfolioImplementationRows(
                allocation,
                annualRow.getProjectedBalance(),
                style
        );
        annualCapitalBreakdownTable.setItems(FXCollections.observableArrayList(rows));

        annualCapitalBreakdownSummary.setText(
                annualRow.getYear() + " • Age " + annualRow.getAge() + " • Projected portfolio " +
                        currencyFormatter.format(annualRow.getProjectedBalance()) + " • " +
                        annualRow.getStocks() + "% stocks / " + annualRow.getBonds() + "% bonds / " +
                        annualRow.getCash() + "% cash • " + style.getDisplayName()
        );
    }

    private TableCell<AnnualAllocationRow, Number> currencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : currencyFormatter.format(value.doubleValue()));
            }
        };
    }

    // ============================================================
    // RISK & SCENARIO ANALYSIS TAB
    // ============================================================

    private ScrollPane createRiskAndScenarioTabContent() {
        Label title = pageTitle("Risk & Scenario Analysis");
        Label description = pageDescription(
                "A single projected balance can create false precision. This section adds inflation-adjusted wealth, " +
                        "risk metrics, capital-market range scenarios and a 5,000-path Monte Carlo simulation."
        );

        GridPane metrics = resultGrid();
        addResultRow(metrics, 0, "Current Expected Return", riskExpectedReturnOutput);
        addResultRow(metrics, 1, "Expected Volatility", currentVolatilityOutput);
        addResultRow(metrics, 2, "Expected Real Return", currentRealReturnOutput);
        addResultRow(metrics, 3, "Illustrative Sharpe Ratio", currentSharpeOutput);
        addResultRow(metrics, 4, "Retirement Balance (Today's Dollars)", riskRealBalanceOutput);

        VBox metricsCard = new VBox(12, cardHeading("Risk-Adjusted Portfolio Metrics"), metrics);
        styleCard(metricsCard);

        GridPane readiness = resultGrid();
        addResultRow(readiness, 0, "Required Retirement Nest Egg", requiredNestEggOutput);
        addResultRow(readiness, 1, "Estimated Sustainable Annual Income", sustainableIncomeOutput);
        addResultRow(readiness, 2, "Annual Retirement Income Gap / Surplus", retirementIncomeGapOutput);
        addResultRow(readiness, 3, "Readiness Ratio", readinessRatioOutput);
        addResultRow(readiness, 4, "Additional Annual Savings Needed", additionalAnnualSavingsOutput);
        VBox readinessCard = new VBox(12, cardHeading("Retirement Goal & Readiness"), readinessStatusOutput, readiness);
        styleCard(readinessCard);

        GridPane fees = resultGrid();
        addResultRow(fees, 0, "Projected Wealth Before Fund Fees", grossEndingBalanceOutput);
        addResultRow(fees, 1, "Projected Wealth After Fund Fees", netEndingBalanceOutput);
        addResultRow(fees, 2, "Estimated Long-Term Fee Impact", cumulativeFeeImpactOutput);
        addResultRow(fees, 3, "Fee Impact vs. Gross Wealth", feeImpactPercentOutput);
        Label feeNote = new Label("Fee impact includes both direct expense-ratio drag and the foregone compounding on those fees. It is an estimate, not an account statement.");
        feeNote.setWrapText(true);
        feeNote.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");
        VBox feeCard = new VBox(12, cardHeading("Long-Term Fee Impact"), fees, feeNote);
        styleCard(feeCard);

        GridPane mc = resultGrid();
        addResultRow(mc, 0, "10th Percentile Outcome", monteCarloP10Output);
        addResultRow(mc, 1, "Median Outcome", monteCarloMedianOutput);
        addResultRow(mc, 2, "90th Percentile Outcome", monteCarloP90Output);
        addResultRow(mc, 3, "Probability of Reaching Goal", goalProbabilityOutput);

        Label mcNote = new Label(
                "Monte Carlo outcomes are generated from 5,000 simulated return paths using the model's expected returns, " +
                        "the volatility assumptions entered in Portfolio Builder, and a 0.10 stock/bond correlation assumption."
        );
        mcNote.setWrapText(true);
        mcNote.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        VBox mcCard = new VBox(12, cardHeading("Monte Carlo Retirement Outcomes"), mc, mcNote);
        styleCard(mcCard);

        GridPane scenarios = resultGrid();
        addResultRow(scenarios, 0, "Conservative (4.2% stocks / 3.8% bonds)", conservativeScenarioOutput);
        addResultRow(scenarios, 1, "Base (5.2% stocks / 4.3% bonds)", baseScenarioOutput);
        addResultRow(scenarios, 2, "Optimistic (6.2% stocks / 4.8% bonds)", optimisticScenarioOutput);

        Label scenarioNote = new Label(
                "The three deterministic scenarios use the lower bound, midpoint and upper bound of the supplied " +
                        "10-year U.S. stock and core-bond forecast ranges. They are scenarios, not probability estimates."
        );
        scenarioNote.setWrapText(true);
        scenarioNote.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 10.5px;");

        VBox scenarioCard = new VBox(12, cardHeading("Capital Market Scenario Analysis"), scenarios, scenarioNote);
        styleCard(scenarioCard);

        VBox aboutCard = new VBox(
                10,
                cardHeading("About This Model"),
                educationParagraph("Developed by Luke Slattery, Finance Student."),
                educationParagraph(
                        "Built in Java/JavaFX to explore target-date portfolio construction, dynamic glide paths, " +
                                "forward-looking capital market assumptions, annual capital allocation, inflation-adjusted wealth, " +
                                "risk-adjusted return metrics and Monte Carlo retirement outcome modeling."
                ),
                educationParagraph(
                        "This application is an educational modeling project and does not provide personalized investment, tax, legal, accounting, or fiduciary advice. " +
                                "Expected returns, volatility and correlations are assumptions and actual market outcomes can differ materially."
                )
        );
        styleCard(aboutCard);

        riskStatusLabel.setText("Calculate a portfolio to populate risk and scenario analytics.");
        riskStatusLabel.setStyle("-fx-text-fill: " + MUTED + ";");

        HBox upper = new HBox(16, metricsCard, mcCard);
        HBox.setHgrow(metricsCard, Priority.ALWAYS);
        HBox.setHgrow(mcCard, Priority.ALWAYS);
        metricsCard.setMinWidth(500);
        mcCard.setMinWidth(500);

        HBox planning = new HBox(16, readinessCard, feeCard);
        HBox.setHgrow(readinessCard, Priority.ALWAYS);
        HBox.setHgrow(feeCard, Priority.ALWAYS);
        readinessCard.setMinWidth(500);
        feeCard.setMinWidth(500);

        VBox content = new VBox(16, title, description, riskStatusLabel, planning, upper, scenarioCard, aboutCard);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    // ============================================================
    // METHODOLOGY & ASSUMPTIONS TAB
    // ============================================================

    private ScrollPane createMethodologyTabContent() {
        Label title = pageTitle("Methodology & Assumptions");
        Label description = pageDescription(
                "This page documents how the model converts investor inputs into portfolio allocations, contributions, " +
                        "return assumptions, risk estimates and retirement-readiness outputs."
        );

        VBox methodology = new VBox(10,
                cardHeading("Projection Methodology"),
                educationParagraph("Glide path: the allocation engine changes stocks, bonds and cash as the investor approaches retirement. The selected glide path is used consistently in the deterministic projection, annual allocation plan, scenarios and Monte Carlo simulation."),
                educationParagraph("Contributions: employee contributions equal salary multiplied by the selected contribution rate. Salary compounds annually at the salary-growth assumption. Employer match equals the match rate multiplied by the lesser of the employee contribution rate and employer match cap."),
                educationParagraph("Return calculation: each year's expected portfolio return is the weighted average of stock, bond and cash assumptions based on that year's glide-path allocation."),
                educationParagraph("Fund fees: the selected funds' expense ratios are weighted by each year's allocation and subtracted from modeled portfolio returns. The fee-impact estimate compares otherwise identical gross and net projections."),
                educationParagraph("Retirement readiness: the model divides desired annual retirement income by the planning withdrawal rate to estimate a required nest egg, then compares base-case projected wealth with that amount."),
                educationParagraph("Monte Carlo: 5,000 simulated paths apply random stock, bond and cash return shocks using user-entered volatility assumptions and a 0.10 stock/bond correlation. Results are distributions, not forecasts.")
        );
        styleCard(methodology);

        GridPane assumptions = resultGrid();
        Label stockA = outputLabel(); stockA.setText("5.20% base | 4.20% conservative | 6.20% optimistic");
        Label bondA = outputLabel(); bondA.setText("4.30% base | 3.80% conservative | 4.80% optimistic");
        Label corrA = outputLabel(); corrA.setText("0.10 stock/bond correlation");
        Label simA = outputLabel(); simA.setText(String.format(Locale.US, "%,d paths", MONTE_CARLO_SIMULATIONS));
        Label feeA = outputLabel(); feeA.setText("Selected fund expense ratios; allocation-weighted annually");
        addResultRow(assumptions, 0, "U.S. Stock Return Assumptions", stockA);
        addResultRow(assumptions, 1, "Core Bond Return Assumptions", bondA);
        addResultRow(assumptions, 2, "Monte Carlo Correlation", corrA);
        addResultRow(assumptions, 3, "Monte Carlo Simulations", simA);
        addResultRow(assumptions, 4, "Fund-Fee Treatment", feeA);
        VBox assumptionsCard = new VBox(12, cardHeading("Core Model Assumptions"), assumptions,
                educationParagraph("Cash return, inflation, volatility, withdrawal rate, salary growth and employer-match assumptions are editable in Portfolio Builder so the user can test different planning cases."));
        styleCard(assumptionsCard);

        VBox limitations = new VBox(10,
                cardHeading("Important Limitations"),
                educationParagraph("The model is educational and does not constitute investment, tax, legal, accounting, or fiduciary advice. Capital-market assumptions can be wrong and actual returns can differ materially."),
                educationParagraph("Benchmark names are used only to describe modeled market exposures. TargetPath is not affiliated with or endorsed by CRSP, S&P Dow Jones Indices, Bloomberg, or any financial institution."),
                educationParagraph("The model does not currently estimate taxes, Social Security, pensions, required minimum distributions, contribution-limit changes, sequence-specific retirement withdrawals, advisory fees or investor-specific tax location."),
                educationParagraph("The planning withdrawal rate is a simplifying assumption for translating wealth into a retirement-income target. It is not a guarantee that a portfolio can sustain that withdrawal indefinitely."),
                educationParagraph("Monte Carlo results are sensitive to expected returns, volatility, correlations and the assumed return distribution. Extreme market events may occur more often than a normal distribution implies.")
        );
        styleCard(limitations);

        VBox attribution = new VBox(8,
                cardHeading("Project Attribution"),
                educationParagraph("Created by Luke Slattery, Finance Student."),
                educationParagraph("Independent Java/JavaFX finance project focused on target-date portfolio construction, asset allocation, retirement modeling, risk analysis and investment implementation.")
        );
        styleCard(attribution);

        VBox content = new VBox(16, title, description, methodology, assumptionsCard, limitations, attribution);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        return scroll;
    }

    // ============================================================
    // INPUT CONFIGURATION
    // ============================================================

    private void configureInputs() {
        incomeSlider.setMajorTickUnit(1);
        incomeSlider.setMinorTickCount(0);
        incomeSlider.setBlockIncrement(1);
        incomeSlider.setSnapToTicks(true);
        incomeSlider.setShowTickMarks(true);
        incomeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            int rounded = (int) Math.round(newValue.doubleValue());
            if (incomeSlider.getValue() != rounded) {
                incomeSlider.setValue(rounded);
            }
            updateIncomeDisplay();
        });

        ageInput.setTextFormatter(integerFormatter(3));
        retirementYearInput.setTextFormatter(integerFormatter(4));
        currentBalanceInput.setTextFormatter(decimalFormatter(12, 2));
        contributionRateInput.setTextFormatter(decimalFormatter(3, 2));
        expenseRatioCeilingInput.setTextFormatter(decimalFormatter(3, 3));
        cashExpectedReturnInput.setTextFormatter(signedDecimalFormatter(3, 2));
        inflationRateInput.setTextFormatter(decimalFormatter(3, 2));
        stockVolatilityInput.setTextFormatter(decimalFormatter(3, 2));
        bondVolatilityInput.setTextFormatter(decimalFormatter(3, 2));
        cashVolatilityInput.setTextFormatter(decimalFormatter(3, 2));
        retirementGoalInput.setTextFormatter(decimalFormatter(12, 2));
        desiredRetirementIncomeInput.setTextFormatter(decimalFormatter(12, 2));
        withdrawalRateInput.setTextFormatter(decimalFormatter(3, 2));
        salaryGrowthInput.setTextFormatter(signedDecimalFormatter(3, 2));
        employerMatchRateInput.setTextFormatter(decimalFormatter(3, 2));
        employerMatchCapInput.setTextFormatter(decimalFormatter(3, 2));

        glidePathSelector.setItems(FXCollections.observableArrayList(
                GlidePathType.TO_RETIREMENT,
                GlidePathType.THROUGH_RETIREMENT
        ));
        glidePathSelector.setValue(GlidePathType.THROUGH_RETIREMENT);
        preferIndexFundsCheckBox.setSelected(true);

        portfolioStyleSelector.setItems(FXCollections.observableArrayList(PortfolioStyle.values()));
        portfolioStyleSelector.setValue(PortfolioStyle.DIVERSIFIED);
        portfolioStyleSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (latestProjection != null) {
                updatePortfolioImplementationForYear(portfolioYearSelector.getValue());
                AnnualAllocationRow selected = allocationTable.getSelectionModel().getSelectedItem();
                if (selected != null) updateAnnualCapitalBreakdown(selected);
            } else if (latestCalculatedAllocation != null) {
                updatePortfolioImplementation(latestCalculatedAllocation, latestCalculatedBalance);
            }
        });

        portfolioYearSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && latestProjection != null) {
                updatePortfolioImplementationForYear(newValue);
                AnnualAllocationRow row = findAnnualRow(newValue);
                if (row != null && allocationTable.getSelectionModel().getSelectedItem() != row) {
                    allocationTable.getSelectionModel().select(row);
                    allocationTable.scrollTo(row);
                }
            }
        });
    }

    // ============================================================
    // MAIN CALCULATION
    // ============================================================

    private void calculateModelPortfolio() {
        clearBuilderError();

        Integer currentAge = parseInteger(ageInput.getText());
        Integer retirementYear = parseInteger(retirementYearInput.getText());
        Double currentBalance = parseDouble(currentBalanceInput.getText());
        Double contributionRatePercent = parseDouble(contributionRateInput.getText());
        Double maximumExpenseRatioPercent = parseDouble(expenseRatioCeilingInput.getText());
        Double cashExpectedReturnPercent = parseDouble(cashExpectedReturnInput.getText());
        Double inflationRatePercent = parseDouble(inflationRateInput.getText());
        Double stockVolatilityPercent = parseDouble(stockVolatilityInput.getText());
        Double bondVolatilityPercent = parseDouble(bondVolatilityInput.getText());
        Double cashVolatilityPercent = parseDouble(cashVolatilityInput.getText());
        Double retirementGoal = parseDouble(retirementGoalInput.getText());
        Double desiredRetirementIncome = parseDouble(desiredRetirementIncomeInput.getText());
        Double withdrawalRatePercent = parseDouble(withdrawalRateInput.getText());
        Double salaryGrowthPercent = parseDouble(salaryGrowthInput.getText());
        Double employerMatchRatePercent = parseDouble(employerMatchRateInput.getText());
        Double employerMatchCapPercent = parseDouble(employerMatchCapInput.getText());

        int currentYear = Year.now().getValue();

        if (currentAge == null || currentAge < 18 || currentAge > 100) {
            showBuilderError("Enter an age between 18 and 100.");
            return;
        }
        if (retirementYear == null || retirementYear < currentYear || retirementYear > currentYear + 100) {
            showBuilderError("Enter a retirement year between " + currentYear + " and " + (currentYear + 100) + ".");
            return;
        }
        if (currentBalance == null || currentBalance < 0) {
            showBuilderError("Enter a valid current portfolio balance.");
            return;
        }
        if (contributionRatePercent == null || contributionRatePercent <= 0 || contributionRatePercent > 100) {
            showBuilderError("Contribution rate must be greater than 0% and no more than 100%.");
            return;
        }
        if (maximumExpenseRatioPercent == null || maximumExpenseRatioPercent < 0 || maximumExpenseRatioPercent > 5) {
            showBuilderError("Maximum expense ratio must be between 0% and 5%.");
            return;
        }
        if (cashExpectedReturnPercent == null || cashExpectedReturnPercent < -20 || cashExpectedReturnPercent > 20) {
            showBuilderError("Cash expected return must be between -20% and 20%.");
            return;
        }

        if (inflationRatePercent == null || inflationRatePercent < 0 || inflationRatePercent > 15) {
            showBuilderError("Inflation rate must be between 0% and 15%.");
            return;
        }
        if (stockVolatilityPercent == null || stockVolatilityPercent <= 0 || stockVolatilityPercent > 60) {
            showBuilderError("Stock volatility must be greater than 0% and no more than 60%.");
            return;
        }
        if (bondVolatilityPercent == null || bondVolatilityPercent <= 0 || bondVolatilityPercent > 30) {
            showBuilderError("Bond volatility must be greater than 0% and no more than 30%.");
            return;
        }
        if (cashVolatilityPercent == null || cashVolatilityPercent < 0 || cashVolatilityPercent > 10) {
            showBuilderError("Cash volatility must be between 0% and 10%.");
            return;
        }
        if (retirementGoal == null || retirementGoal <= 0) {
            showBuilderError("Enter a retirement goal greater than $0.");
            return;
        }
        if (desiredRetirementIncome == null || desiredRetirementIncome <= 0) {
            showBuilderError("Enter desired annual retirement income greater than $0.");
            return;
        }
        if (withdrawalRatePercent == null || withdrawalRatePercent <= 0 || withdrawalRatePercent > 10) {
            showBuilderError("Planning withdrawal rate must be greater than 0% and no more than 10%.");
            return;
        }
        if (salaryGrowthPercent == null || salaryGrowthPercent < -20 || salaryGrowthPercent > 20) {
            showBuilderError("Annual salary growth must be between -20% and 20%.");
            return;
        }
        if (employerMatchRatePercent == null || employerMatchRatePercent < 0 || employerMatchRatePercent > 300) {
            showBuilderError("Employer match rate must be between 0% and 300%.");
            return;
        }
        if (employerMatchCapPercent == null || employerMatchCapPercent < 0 || employerMatchCapPercent > 25) {
            showBuilderError("Employer match cap must be between 0% and 25% of salary.");
            return;
        }

        int yearsToRetirement = retirementYear - currentYear;
        int retirementAge = currentAge + yearsToRetirement;
        if (retirementAge > 110) {
            showBuilderError("Projected retirement age is greater than 110.");
            return;
        }

        int incomePosition = (int) Math.round(incomeSlider.getValue());
        long selectedIncome = INCOME_LEVELS[incomePosition];
        double contributionRate = contributionRatePercent / 100.0;
        double salaryGrowthRate = salaryGrowthPercent / 100.0;
        double employerMatchRate = employerMatchRatePercent / 100.0;
        double employerMatchCap = employerMatchCapPercent / 100.0;
        double withdrawalRate = withdrawalRatePercent / 100.0;
        double firstYearEmployeeContribution = selectedIncome * contributionRate;
        double firstYearEmployerMatch = calculateEmployerMatch(selectedIncome, contributionRate, employerMatchRate, employerMatchCap);
        double annualContribution = firstYearEmployeeContribution + firstYearEmployerMatch;
        double monthlyContribution = annualContribution / 12.0;
        double cashExpectedReturn = cashExpectedReturnPercent / 100.0;
        double inflationRate = inflationRatePercent / 100.0;
        double stockVolatility = stockVolatilityPercent / 100.0;
        double bondVolatility = bondVolatilityPercent / 100.0;
        double cashVolatility = cashVolatilityPercent / 100.0;
        double expenseRatioCeiling = maximumExpenseRatioPercent / 100.0;

        GlidePathType glidePathType = glidePathSelector.getValue();
        if (glidePathType == null) {
            glidePathType = GlidePathType.THROUGH_RETIREMENT;
        }

        Allocation currentAllocation = allocationEngine.determineAllocation(yearsToRetirement, glidePathType);
        Allocation retirementAllocation = allocationEngine.determineAllocation(0, glidePathType);

        FundSelectionStrategy selector = new TransparentLowCostFundSelector(
                expenseRatioCeiling,
                preferIndexFundsCheckBox.isSelected(),
                PLATFORM_SPONSOR,
                PROPRIETARY_COST_TOLERANCE
        );

        try {
            Fund stockFund = selector.selectFund(AssetClass.STOCKS, availableFunds);
            Fund bondFund = selector.selectFund(AssetClass.BONDS, availableFunds);
            Fund cashFund = selector.selectFund(AssetClass.CASH, availableFunds);

            ProjectionResult projection = createProjection(
                    currentYear,
                    currentAge,
                    yearsToRetirement,
                    currentBalance,
                    selectedIncome,
                    contributionRate,
                    salaryGrowthRate,
                    employerMatchRate,
                    employerMatchCap,
                    glidePathType,
                    cashExpectedReturn,
                    stockFund,
                    bondFund,
                    cashFund,
                    true
            );

            ProjectionResult grossProjection = createProjection(
                    currentYear,
                    currentAge,
                    yearsToRetirement,
                    currentBalance,
                    selectedIncome,
                    contributionRate,
                    salaryGrowthRate,
                    employerMatchRate,
                    employerMatchCap,
                    glidePathType,
                    cashExpectedReturn,
                    stockFund,
                    bondFund,
                    cashFund,
                    false
            );

            int targetFundYear = roundToNearestFive(retirementYear);
            fundNameOutput.setText("Transparent Target Retirement " + targetFundYear + " Portfolio");
            yearsToRetirementOutput.setText(yearsToRetirement + " years");
            retirementAgeOutput.setText(String.valueOf(retirementAge));
            annualContributionOutput.setText(currencyFormatter.format(annualContribution));
            monthlyContributionOutput.setText(currencyFormatter.format(monthlyContribution));
            firstYearEmployeeContributionOutput.setText(currencyFormatter.format(firstYearEmployeeContribution));
            firstYearEmployerMatchOutput.setText(currencyFormatter.format(firstYearEmployerMatch));
            double retirementYearSalary = selectedIncome * Math.pow(1.0 + salaryGrowthRate, Math.max(0, yearsToRetirement));
            double retirementYearContribution = retirementYearSalary * contributionRate
                    + calculateEmployerMatch(retirementYearSalary, contributionRate, employerMatchRate, employerMatchCap);
            retirementYearContributionOutput.setText(currencyFormatter.format(retirementYearContribution));
            startingExpectedReturnOutput.setText(formatPercentage(
                    calculateExpectedPortfolioReturn(currentAllocation, cashExpectedReturn)
            ));
            retirementExpectedReturnOutput.setText(formatPercentage(
                    calculateExpectedPortfolioReturn(retirementAllocation, cashExpectedReturn)
            ));
            projectedBalanceOutput.setText(currencyFormatter.format(projection.getEndingBalance()));
            totalContributionsOutput.setText(currencyFormatter.format(projection.getTotalPrincipal()));
            projectedGrowthOutput.setText(currencyFormatter.format(projection.getInvestmentGrowth()));
            double realEndingBalance = projection.getEndingBalance() / Math.pow(1.0 + inflationRate, yearsToRetirement);
            realProjectedBalanceOutput.setText(currencyFormatter.format(realEndingBalance));

            updateRiskAndScenarioAnalysis(
                    currentYear,
                    currentAge,
                    yearsToRetirement,
                    currentBalance,
                    selectedIncome,
                    contributionRate,
                    salaryGrowthRate,
                    employerMatchRate,
                    employerMatchCap,
                    stockFund,
                    bondFund,
                    cashFund,
                    glidePathType,
                    cashExpectedReturn,
                    inflationRate,
                    stockVolatility,
                    bondVolatility,
                    cashVolatility,
                    retirementGoal,
                    desiredRetirementIncome,
                    withdrawalRate,
                    currentAllocation,
                    projection,
                    grossProjection
            );
            glidePathOutput.setText(glidePathType.getDisplayName());

            updateAllocationDisplay(currentAllocation);
            updateFundDisplay(stockFund, bondFund, cashFund, currentAllocation, currentBalance);
            latestProjection = projection;
            updateProjectionChart(projection);
            updateAllocationPlan(projection);
            updatePortfolioImplementation(currentAllocation, currentBalance);

            allocationPlanStatus.setText(
                    "Annual plan generated through " + retirementYear + ". The schedule uses the same glide path and " +
                            "return assumptions as the retirement projection."
            );
            allocationPlanStatus.setStyle(
                    "-fx-background-color: #E8F4EE; -fx-background-radius: 8px; -fx-padding: 12px;" +
                            "-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;"
            );

        } catch (NoSuchElementException ex) {
            showBuilderError(ex.getMessage() + " Increase the expense-ratio ceiling or add another eligible fund.");
        }
    }

    // ============================================================
    // PROJECTION + ANNUAL ALLOCATION LOGIC
    // ============================================================

    private ProjectionResult createProjection(
            int currentYear,
            int currentAge,
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            GlidePathType glidePathType,
            double cashExpectedReturn,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund,
            boolean includeFundFees
    ) {
        List<ProjectionPoint> points = new ArrayList<>();
        List<AnnualAllocationRow> annualRows = new ArrayList<>();
        double balance = startingBalance;
        double totalContributions = 0.0;

        Allocation startingAllocation = allocationEngine.determineAllocation(yearsToRetirement, glidePathType);
        double startingReturn = calculateExpectedPortfolioReturn(startingAllocation, cashExpectedReturn);
        points.add(new ProjectionPoint(currentYear, balance));
        double startingPlannedContribution = startingIncome * contributionRate
                + calculateEmployerMatch(startingIncome, contributionRate, employerMatchRate, employerMatchCap);
        annualRows.add(new AnnualAllocationRow(currentYear, currentAge, yearsToRetirement,
                startingAllocation, startingReturn, balance, startingPlannedContribution));

        for (int yearNumber = 1; yearNumber <= yearsToRetirement; yearNumber++) {
            int remainingBeforeYear = yearsToRetirement - yearNumber + 1;
            Allocation investmentAllocation = allocationEngine.determineAllocation(remainingBeforeYear, glidePathType);
            double grossReturn = calculateExpectedPortfolioReturn(investmentAllocation, cashExpectedReturn);
            double expenseDrag = includeFundFees
                    ? calculateWeightedExpenseRatio(investmentAllocation, stockFund, bondFund, cashFund)
                    : 0.0;
            double netReturn = grossReturn - expenseDrag;

            double salaryThisYear = startingIncome * Math.pow(1.0 + salaryGrowthRate, yearNumber - 1);
            double employeeContribution = salaryThisYear * contributionRate;
            double employerMatch = calculateEmployerMatch(
                    salaryThisYear, contributionRate, employerMatchRate, employerMatchCap
            );
            double totalYearContribution = employeeContribution + employerMatch;
            totalContributions += totalYearContribution;

            balance = balance
                    + balance * netReturn
                    + totalYearContribution
                    + totalYearContribution * netReturn * 0.5;
            balance = Math.max(0.0, balance);

            int rowYear = currentYear + yearNumber;
            int rowAge = currentAge + yearNumber;
            int remainingAtYearEnd = yearsToRetirement - yearNumber;
            Allocation yearEndAllocation = allocationEngine.determineAllocation(remainingAtYearEnd, glidePathType);
            double yearEndExpectedReturn = calculateExpectedPortfolioReturn(yearEndAllocation, cashExpectedReturn)
                    - (includeFundFees ? calculateWeightedExpenseRatio(yearEndAllocation, stockFund, bondFund, cashFund) : 0.0);

            points.add(new ProjectionPoint(rowYear, balance));
            annualRows.add(new AnnualAllocationRow(rowYear, rowAge, remainingAtYearEnd,
                    yearEndAllocation, yearEndExpectedReturn, balance, totalYearContribution));
        }

        double totalPrincipal = startingBalance + totalContributions;
        double investmentGrowth = balance - totalPrincipal;
        return new ProjectionResult(points, annualRows, balance, totalPrincipal, investmentGrowth);
    }

    private double calculateEmployerMatch(
            double salary,
            double contributionRate,
            double employerMatchRate,
            double employerMatchCap
    ) {
        double matchedContributionRate = Math.min(Math.max(0.0, contributionRate), Math.max(0.0, employerMatchCap));
        return salary * matchedContributionRate * Math.max(0.0, employerMatchRate);
    }

    private double calculateWeightedExpenseRatio(
            Allocation allocation,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund
    ) {
        return allocation.getStocks() / 100.0 * stockFund.getExpenseRatio()
                + allocation.getBonds() / 100.0 * bondFund.getExpenseRatio()
                + allocation.getCash() / 100.0 * cashFund.getExpenseRatio();
    }

    private double calculateExpectedPortfolioReturn(Allocation allocation, double cashExpectedReturn) {
        return allocation.getStocks() / 100.0 * STOCK_EXPECTED_RETURN
                + allocation.getBonds() / 100.0 * BOND_EXPECTED_RETURN
                + allocation.getCash() / 100.0 * cashExpectedReturn;
    }

    private void updateAllocationPlan(ProjectionResult projection) {
        allocationTable.setItems(FXCollections.observableArrayList(projection.getAnnualRows()));

        if (!projection.getAnnualRows().isEmpty()) {
            allocationTable.getSelectionModel().selectFirst();
            updateAnnualCapitalBreakdown(projection.getAnnualRows().get(0));
            AnnualAllocationRow first = projection.getAnnualRows().get(0);
            AnnualAllocationRow last = projection.getAnnualRows().get(projection.getAnnualRows().size() - 1);
            allocationPlanSummary.setText(
                    "The plan begins at " + first.getStocks() + "% stocks / " + first.getBonds() + "% bonds / " +
                            first.getCash() + "% cash and reaches " + last.getStocks() + "% stocks / " +
                            last.getBonds() + "% bonds / " + last.getCash() + "% cash at retirement. " +
                            "The dollar targets update with the projected portfolio balance each year."
            );
        }
    }

    // ============================================================
    // RISK / MONTE CARLO / SCENARIO LOGIC
    // ============================================================

    private void updateRiskAndScenarioAnalysis(
            int currentYear,
            int currentAge,
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund,
            GlidePathType glidePathType,
            double cashExpectedReturn,
            double inflationRate,
            double stockVolatility,
            double bondVolatility,
            double cashVolatility,
            double retirementGoal,
            double desiredRetirementIncome,
            double withdrawalRate,
            Allocation currentAllocation,
            ProjectionResult baseProjection,
            ProjectionResult grossProjection
    ) {
        double expectedReturn = calculateExpectedPortfolioReturn(currentAllocation, cashExpectedReturn);
        double expectedVolatility = calculatePortfolioVolatility(
                currentAllocation, stockVolatility, bondVolatility, cashVolatility
        );
        double realReturn = (1.0 + expectedReturn) / (1.0 + inflationRate) - 1.0;
        double sharpe = expectedVolatility <= 0.000001 ? 0.0 : (expectedReturn - cashExpectedReturn) / expectedVolatility;

        riskExpectedReturnOutput.setText(formatPercentage(expectedReturn));
        double riskRealBalance = baseProjection.getEndingBalance() / Math.pow(1.0 + inflationRate, yearsToRetirement);
        riskRealBalanceOutput.setText(currencyFormatter.format(riskRealBalance));
        currentVolatilityOutput.setText(formatPercentage(expectedVolatility));
        currentRealReturnOutput.setText(formatPercentage(realReturn));
        currentSharpeOutput.setText(String.format(Locale.US, "%.2f", sharpe));

        double requiredNestEgg = desiredRetirementIncome / withdrawalRate;
        double sustainableIncome = baseProjection.getEndingBalance() * withdrawalRate;
        double incomeGap = sustainableIncome - desiredRetirementIncome;
        double readinessRatio = requiredNestEgg <= 0 ? 0.0 : baseProjection.getEndingBalance() / requiredNestEgg;
        double additionalAnnualSavings = calculateAdditionalAnnualSavingsNeeded(
                requiredNestEgg, yearsToRetirement, startingBalance, startingIncome, contributionRate,
                salaryGrowthRate, employerMatchRate, employerMatchCap, glidePathType, cashExpectedReturn,
                stockFund, bondFund, cashFund
        );

        requiredNestEggOutput.setText(currencyFormatter.format(requiredNestEgg));
        sustainableIncomeOutput.setText(currencyFormatter.format(sustainableIncome));
        retirementIncomeGapOutput.setText((incomeGap >= 0 ? "+" : "") + currencyFormatter.format(incomeGap));
        readinessRatioOutput.setText(String.format(Locale.US, "%.0f%%", readinessRatio * 100.0));
        additionalAnnualSavingsOutput.setText(additionalAnnualSavings <= 0.01
                ? "$0 — Base projection meets the modeled income target"
                : currencyFormatter.format(additionalAnnualSavings) + " per year initially");

        if (readinessRatio >= 1.10) {
            readinessStatusOutput.setText("ON TRACK — Base-case projected wealth exceeds the modeled nest-egg requirement by at least 10%.");
            readinessStatusOutput.setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
        } else if (readinessRatio >= 0.90) {
            readinessStatusOutput.setText("NEAR TARGET — Base-case projected wealth is within 10% of the modeled nest-egg requirement.");
            readinessStatusOutput.setStyle("-fx-text-fill: #8A5A00; -fx-font-weight: bold;");
        } else {
            readinessStatusOutput.setText("BELOW TARGET — Base-case projected wealth is more than 10% below the modeled nest-egg requirement.");
            readinessStatusOutput.setStyle("-fx-text-fill: " + ERROR + "; -fx-font-weight: bold;");
        }

        grossEndingBalanceOutput.setText(currencyFormatter.format(grossProjection.getEndingBalance()));
        netEndingBalanceOutput.setText(currencyFormatter.format(baseProjection.getEndingBalance()));
        double feeImpact = Math.max(0.0, grossProjection.getEndingBalance() - baseProjection.getEndingBalance());
        cumulativeFeeImpactOutput.setText(currencyFormatter.format(feeImpact));
        feeImpactPercentOutput.setText(grossProjection.getEndingBalance() <= 0 ? "0.00%"
                : formatAllocationPercent(feeImpact / grossProjection.getEndingBalance() * 100.0));

        double conservative = calculateScenarioEndingBalance(
                yearsToRetirement, startingBalance, startingIncome, contributionRate, salaryGrowthRate,
                employerMatchRate, employerMatchCap, glidePathType, STOCK_LOW_RETURN, BOND_LOW_RETURN,
                cashExpectedReturn, stockFund, bondFund, cashFund
        );
        double optimistic = calculateScenarioEndingBalance(
                yearsToRetirement, startingBalance, startingIncome, contributionRate, salaryGrowthRate,
                employerMatchRate, employerMatchCap, glidePathType, STOCK_HIGH_RETURN, BOND_HIGH_RETURN,
                cashExpectedReturn, stockFund, bondFund, cashFund
        );

        conservativeScenarioOutput.setText(currencyFormatter.format(conservative));
        baseScenarioOutput.setText(currencyFormatter.format(baseProjection.getEndingBalance()));
        optimisticScenarioOutput.setText(currencyFormatter.format(optimistic));

        MonteCarloResult monteCarlo = runMonteCarlo(
                yearsToRetirement, startingBalance, startingIncome, contributionRate, salaryGrowthRate,
                employerMatchRate, employerMatchCap, glidePathType, cashExpectedReturn,
                stockVolatility, bondVolatility, cashVolatility, retirementGoal,
                stockFund, bondFund, cashFund
        );
        monteCarloP10Output.setText(currencyFormatter.format(monteCarlo.getP10()));
        monteCarloMedianOutput.setText(currencyFormatter.format(monteCarlo.getMedian()));
        monteCarloP90Output.setText(currencyFormatter.format(monteCarlo.getP90()));
        goalProbabilityOutput.setText(String.format(Locale.US, "%.1f%%", monteCarlo.getGoalProbability() * 100.0));

        riskStatusLabel.setText(
                "Risk, readiness, fee and scenario analytics updated using the same salary-growth, employer-match, " +
                        "fund-fee and glide-path assumptions as the base projection."
        );
        riskStatusLabel.setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
    }

    private double calculateAdditionalAnnualSavingsNeeded(
            double targetBalance,
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            GlidePathType glidePathType,
            double cashExpectedReturn,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund
    ) {
        if (yearsToRetirement <= 0) return Math.max(0.0, targetBalance - startingBalance);
        ProjectionResult base = createProjection(Year.now().getValue(), 0, yearsToRetirement, startingBalance,
                startingIncome, contributionRate, salaryGrowthRate, employerMatchRate, employerMatchCap,
                glidePathType, cashExpectedReturn, stockFund, bondFund, cashFund, true);
        if (base.getEndingBalance() >= targetBalance) return 0.0;

        double low = 0.0;
        double high = Math.max(10_000.0, startingIncome);
        while (endingBalanceWithExtraSavings(high, yearsToRetirement, startingBalance, startingIncome,
                contributionRate, salaryGrowthRate, employerMatchRate, employerMatchCap, glidePathType,
                cashExpectedReturn, stockFund, bondFund, cashFund) < targetBalance && high < 10_000_000.0) {
            high *= 2.0;
        }
        for (int i = 0; i < 60; i++) {
            double mid = (low + high) / 2.0;
            double ending = endingBalanceWithExtraSavings(mid, yearsToRetirement, startingBalance, startingIncome,
                    contributionRate, salaryGrowthRate, employerMatchRate, employerMatchCap, glidePathType,
                    cashExpectedReturn, stockFund, bondFund, cashFund);
            if (ending >= targetBalance) high = mid; else low = mid;
        }
        return high;
    }

    private double endingBalanceWithExtraSavings(
            double firstYearExtraSavings,
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            GlidePathType glidePathType,
            double cashExpectedReturn,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund
    ) {
        double balance = startingBalance;
        for (int yearNumber = 1; yearNumber <= yearsToRetirement; yearNumber++) {
            int remaining = yearsToRetirement - yearNumber + 1;
            Allocation allocation = allocationEngine.determineAllocation(remaining, glidePathType);
            double netReturn = calculateExpectedPortfolioReturn(allocation, cashExpectedReturn)
                    - calculateWeightedExpenseRatio(allocation, stockFund, bondFund, cashFund);
            double salary = startingIncome * Math.pow(1.0 + salaryGrowthRate, yearNumber - 1);
            double regular = salary * contributionRate
                    + calculateEmployerMatch(salary, contributionRate, employerMatchRate, employerMatchCap);
            double extra = firstYearExtraSavings * Math.pow(1.0 + salaryGrowthRate, yearNumber - 1);
            double contribution = regular + extra;
            balance += balance * netReturn + contribution + contribution * netReturn * 0.5;
            balance = Math.max(0.0, balance);
        }
        return balance;
    }

    private double calculatePortfolioVolatility(
            Allocation allocation,
            double stockVolatility,
            double bondVolatility,
            double cashVolatility
    ) {
        double ws = allocation.getStocks() / 100.0;
        double wb = allocation.getBonds() / 100.0;
        double wc = allocation.getCash() / 100.0;
        double variance = ws * ws * stockVolatility * stockVolatility
                + wb * wb * bondVolatility * bondVolatility
                + wc * wc * cashVolatility * cashVolatility
                + 2.0 * ws * wb * STOCK_BOND_CORRELATION * stockVolatility * bondVolatility;
        return Math.sqrt(Math.max(0.0, variance));
    }

    private double calculateScenarioEndingBalance(
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            GlidePathType glidePathType,
            double stockReturn,
            double bondReturn,
            double cashReturn,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund
    ) {
        double balance = startingBalance;
        for (int yearNumber = 1; yearNumber <= yearsToRetirement; yearNumber++) {
            int remaining = yearsToRetirement - yearNumber + 1;
            Allocation allocation = allocationEngine.determineAllocation(remaining, glidePathType);
            double yearlyReturn = calculateExpectedPortfolioReturn(allocation, stockReturn, bondReturn, cashReturn)
                    - calculateWeightedExpenseRatio(allocation, stockFund, bondFund, cashFund);
            double salary = startingIncome * Math.pow(1.0 + salaryGrowthRate, yearNumber - 1);
            double contribution = salary * contributionRate
                    + calculateEmployerMatch(salary, contributionRate, employerMatchRate, employerMatchCap);
            balance += balance * yearlyReturn + contribution + contribution * yearlyReturn * 0.5;
            balance = Math.max(0.0, balance);
        }
        return balance;
    }

    private MonteCarloResult runMonteCarlo(
            int yearsToRetirement,
            double startingBalance,
            double startingIncome,
            double contributionRate,
            double salaryGrowthRate,
            double employerMatchRate,
            double employerMatchCap,
            GlidePathType glidePathType,
            double cashExpectedReturn,
            double stockVolatility,
            double bondVolatility,
            double cashVolatility,
            double retirementGoal,
            Fund stockFund,
            Fund bondFund,
            Fund cashFund
    ) {
        double[] outcomes = new double[MONTE_CARLO_SIMULATIONS];
        int goalHits = 0;
        Random random = new Random(42L);
        double independentBondScale = Math.sqrt(1.0 - STOCK_BOND_CORRELATION * STOCK_BOND_CORRELATION);

        for (int simulation = 0; simulation < MONTE_CARLO_SIMULATIONS; simulation++) {
            double balance = startingBalance;
            for (int yearNumber = 1; yearNumber <= yearsToRetirement; yearNumber++) {
                int remaining = yearsToRetirement - yearNumber + 1;
                Allocation allocation = allocationEngine.determineAllocation(remaining, glidePathType);
                double stockShock = random.nextGaussian();
                double bondShock = STOCK_BOND_CORRELATION * stockShock + independentBondScale * random.nextGaussian();
                double cashShock = random.nextGaussian();
                double stockReturn = STOCK_EXPECTED_RETURN + stockVolatility * stockShock;
                double bondReturn = BOND_EXPECTED_RETURN + bondVolatility * bondShock;
                double cashReturn = cashExpectedReturn + cashVolatility * cashShock;
                double portfolioReturn = calculateExpectedPortfolioReturn(allocation, stockReturn, bondReturn, cashReturn)
                        - calculateWeightedExpenseRatio(allocation, stockFund, bondFund, cashFund);
                portfolioReturn = Math.max(-0.95, portfolioReturn);
                double salary = startingIncome * Math.pow(1.0 + salaryGrowthRate, yearNumber - 1);
                double contribution = salary * contributionRate
                        + calculateEmployerMatch(salary, contributionRate, employerMatchRate, employerMatchCap);
                balance += balance * portfolioReturn + contribution + contribution * portfolioReturn * 0.5;
                balance = Math.max(0.0, balance);
            }
            outcomes[simulation] = balance;
            if (balance >= retirementGoal) goalHits++;
        }
        Arrays.sort(outcomes);
        return new MonteCarloResult(percentile(outcomes, 0.10), percentile(outcomes, 0.50),
                percentile(outcomes, 0.90), goalHits / (double) MONTE_CARLO_SIMULATIONS);
    }

    private double percentile(double[] sortedValues, double percentile) {
        if (sortedValues.length == 0) return 0.0;
        double index = percentile * (sortedValues.length - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) return sortedValues[lower];
        double weight = index - lower;
        return sortedValues[lower] * (1.0 - weight) + sortedValues[upper] * weight;
    }

    private double calculateExpectedPortfolioReturn(
            Allocation allocation,
            double stockReturn,
            double bondReturn,
            double cashReturn
    ) {
        return allocation.getStocks() / 100.0 * stockReturn
                + allocation.getBonds() / 100.0 * bondReturn
                + allocation.getCash() / 100.0 * cashReturn;
    }

    // ============================================================
    // CHART
    // ============================================================

    private void configureProjectionChart() {
        chartXAxis.setLabel("Year");
        chartYAxis.setLabel("Projected Portfolio Value");
        chartYAxis.setForceZeroInRange(true);
        chartYAxis.setTickLabelFormatter(new CurrencyAxisFormatter(chartYAxis));
        projectionChart.setTitle("Estimated Growth Through Retirement");
        projectionChart.setLegendVisible(false);
        projectionChart.setCreateSymbols(true);
        projectionChart.setAnimated(false);
        projectionChart.setMinHeight(470);
        projectionChart.setPrefHeight(620);
    }

    private void loadEmptyChart() {
        projectionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(String.valueOf(Year.now().getValue()), 0));
        projectionChart.getData().add(series);
    }

    private void updateProjectionChart(ProjectionResult projection) {
        projectionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Projected Portfolio Value");

        List<ProjectionPoint> points = projection.getPoints();
        int interval;
        if (points.size() <= 16) interval = 1;
        else if (points.size() <= 31) interval = 2;
        else if (points.size() <= 51) interval = 5;
        else interval = 10;

        List<String> categories = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0 || i == points.size() - 1 || i % interval == 0) {
                ProjectionPoint point = points.get(i);
                String year = String.valueOf(point.getYear());
                categories.add(year);
                series.getData().add(new XYChart.Data<>(year, point.getBalance()));
            }
        }
        chartXAxis.setCategories(FXCollections.observableArrayList(categories));
        projectionChart.getData().add(series);
    }

    // ============================================================
    // ALLOCATION + FUND DISPLAY
    // ============================================================

    private void updateAllocationDisplay(Allocation allocation) {
        stockOutput.setText(allocation.getStocks() + "%");
        bondOutput.setText(allocation.getBonds() + "%");
        cashOutput.setText(allocation.getCash() + "%");
        stockBar.setProgress(allocation.getStocks() / 100.0);
        bondBar.setProgress(allocation.getBonds() / 100.0);
        cashBar.setProgress(allocation.getCash() / 100.0);
    }

    private void updateFundDisplay(
            Fund stockFund,
            Fund bondFund,
            Fund cashFund,
            Allocation allocation,
            double currentPortfolioBalance
    ) {
        stockFundOutput.setText(formatFundDisclosure(stockFund, allocation.getStocks()));
        bondFundOutput.setText(formatFundDisclosure(bondFund, allocation.getBonds()));
        cashFundOutput.setText(formatFundDisclosure(cashFund, allocation.getCash()));

        double weightedExpenseRatio = (
                allocation.getStocks() * stockFund.getExpenseRatio()
                        + allocation.getBonds() * bondFund.getExpenseRatio()
                        + allocation.getCash() * cashFund.getExpenseRatio()
        ) / 100.0;

        weightedExpenseRatioOutput.setText(formatPercentage(weightedExpenseRatio));
        estimatedAnnualCostOutput.setText(currencyFormatter.format(currentPortfolioBalance * weightedExpenseRatio));

        List<Fund> proprietary = List.of(stockFund, bondFund, cashFund)
                .stream()
                .filter(Fund::isProprietary)
                .collect(Collectors.toList());

        if (proprietary.isEmpty()) {
            disclosureOutput.setText(
                    "Conflict-of-interest disclosure: No proprietary " + PLATFORM_SPONSOR +
                            " funds were selected. Funds were selected using the stated cost and index preferences."
            );
        } else {
            String tickers = proprietary.stream().map(Fund::getTicker).collect(Collectors.joining(", "));
            disclosureOutput.setText(
                    "Conflict-of-interest disclosure: " + tickers + " is sponsored by " + PLATFORM_SPONSOR +
                            ". It was eligible only because its expense ratio was within the permitted cost tolerance " +
                            "of the lowest-cost outside alternative."
            );
        }
    }

    // ============================================================
    // FUND SELECTION
    // ============================================================

    private interface FundSelectionStrategy {
        Fund selectFund(AssetClass assetClass, List<Fund> candidates);
    }

    private static class TransparentLowCostFundSelector implements FundSelectionStrategy {
        private final double expenseRatioCeiling;
        private final boolean preferIndexFunds;
        private final String platformSponsor;
        private final double proprietaryCostTolerance;

        private TransparentLowCostFundSelector(
                double expenseRatioCeiling,
                boolean preferIndexFunds,
                String platformSponsor,
                double proprietaryCostTolerance
        ) {
            this.expenseRatioCeiling = expenseRatioCeiling;
            this.preferIndexFunds = preferIndexFunds;
            this.platformSponsor = platformSponsor;
            this.proprietaryCostTolerance = proprietaryCostTolerance;
        }

        @Override
        public Fund selectFund(AssetClass assetClass, List<Fund> candidates) {
            List<Fund> eligible = candidates.stream()
                    .filter(f -> f.getAssetClass() == assetClass)
                    .filter(f -> f.getExpenseRatio() <= expenseRatioCeiling)
                    .collect(Collectors.toList());

            if (eligible.isEmpty()) {
                throw new NoSuchElementException(
                        "No " + assetClass.getDisplayName().toLowerCase() +
                                " fund meets the selected expense-ratio ceiling."
                );
            }

            if (preferIndexFunds) {
                List<Fund> indexes = eligible.stream().filter(Fund::isIndexFund).collect(Collectors.toList());
                if (!indexes.isEmpty()) eligible = indexes;
            }

            eligible.sort(Comparator.comparingDouble(Fund::getExpenseRatio).thenComparing(Fund::getTicker));
            Fund lowest = eligible.get(0);
            if (!isPlatformProprietary(lowest)) return lowest;

            Fund outside = eligible.stream()
                    .filter(f -> !isPlatformProprietary(f))
                    .min(Comparator.comparingDouble(Fund::getExpenseRatio))
                    .orElse(null);

            if (outside == null) return lowest;

            double difference = lowest.getExpenseRatio() - outside.getExpenseRatio();
            return difference <= proprietaryCostTolerance ? lowest : outside;
        }

        private boolean isPlatformProprietary(Fund fund) {
            return fund.getSponsor().equalsIgnoreCase(platformSponsor);
        }
    }

    // ============================================================
    // GLIDE PATH ENGINE
    // ============================================================

    private interface AllocationEngine {
        Allocation determineAllocation(int yearsToRetirement, GlidePathType glidePathType);
    }

    private static class AgeBasedAllocationEngine implements AllocationEngine {
        @Override
        public Allocation determineAllocation(int yearsToRetirement, GlidePathType glidePathType) {
            if (glidePathType == GlidePathType.TO_RETIREMENT) {
                return determineToRetirementAllocation(yearsToRetirement);
            }
            return determineThroughRetirementAllocation(yearsToRetirement);
        }

        private Allocation determineToRetirementAllocation(int years) {
            if (years >= 35) return new Allocation(90, 8, 2);
            if (years >= 30) return new Allocation(86, 12, 2);
            if (years >= 25) return new Allocation(82, 16, 2);
            if (years >= 20) return new Allocation(76, 21, 3);
            if (years >= 15) return new Allocation(68, 29, 3);
            if (years >= 10) return new Allocation(60, 36, 4);
            if (years >= 5) return new Allocation(50, 45, 5);
            return new Allocation(40, 53, 7);
        }

        private Allocation determineThroughRetirementAllocation(int years) {
            if (years >= 35) return new Allocation(90, 8, 2);
            if (years >= 30) return new Allocation(88, 10, 2);
            if (years >= 25) return new Allocation(85, 13, 2);
            if (years >= 20) return new Allocation(80, 18, 2);
            if (years >= 15) return new Allocation(75, 22, 3);
            if (years >= 10) return new Allocation(68, 29, 3);
            if (years >= 5) return new Allocation(60, 36, 4);
            return new Allocation(50, 45, 5);
        }
    }

    // ============================================================
    // DEMONSTRATION FUND DATABASE
    // ============================================================

    private List<Fund> createAvailableFunds() {
        /*
         * Broad market exposures and benchmark-based implementation assumptions.
         * These are not recommendations to purchase specific securities.
         * Expense ratios are illustrative assumptions used only to model fee drag.
         */
        return List.of(
                new Fund(
                        "U.S. Total Stock Market",
                        "CRSP U.S. Total Market Index (benchmark reference)",
                        AssetClass.STOCKS,
                        0.0003,
                        true,
                        "Broad U.S. Equity Exposure",
                        false
                ),
                new Fund(
                        "U.S. Large-Cap Equity",
                        "S&P 500 Index (benchmark reference)",
                        AssetClass.STOCKS,
                        0.0003,
                        true,
                        "U.S. Large-Cap Equity Exposure",
                        false
                ),
                new Fund(
                        "Core U.S. Investment-Grade Bonds",
                        "Bloomberg U.S. Aggregate Bond Index (benchmark reference)",
                        AssetClass.BONDS,
                        0.0004,
                        true,
                        "Core Fixed-Income Exposure",
                        false
                ),
                new Fund(
                        "U.S. Treasury Bonds",
                        "Bloomberg U.S. Treasury Index (benchmark reference)",
                        AssetClass.BONDS,
                        0.0005,
                        true,
                        "U.S. Treasury Exposure",
                        false
                ),
                new Fund(
                        "Government Cash & Short-Term Treasuries",
                        "U.S. Treasury Bills / Government Money Market",
                        AssetClass.CASH,
                        0.0008,
                        true,
                        "Cash & Treasury Exposure",
                        false
                )
        );
    }

    // ============================================================
    // THEME / UI HELPERS
    // ============================================================

    private void applyTheme(Scene scene, TabPane tabPane) {
        scene.getRoot().setStyle("-fx-font-family: 'Arial'; -fx-background-color: " + BACKGROUND + ";");
        tabPane.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: transparent;");
        styleNodeTree(scene.getRoot());
    }

    private void styleNodeTree(javafx.scene.Node node) {
        if (node instanceof TextField field) styleTextField(field);
        else if (node instanceof ComboBox<?> combo) styleComboBox(combo);
        else if (node instanceof CheckBox check) {
            check.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-weight: bold;");
        } else if (node instanceof ProgressBar progress) {
            progress.setStyle("-fx-accent: " + GOLD + ";");
        } else if (node instanceof TableView<?> table) {
            table.setStyle(
                    "-fx-background-color: white; -fx-border-color: " + BORDER + ";" +
                            "-fx-border-radius: 8px; -fx-background-radius: 8px;"
            );
        }

        if (node instanceof Pane pane) {
            for (javafx.scene.Node child : pane.getChildren()) styleNodeTree(child);
        } else if (node instanceof ScrollPane scroll && scroll.getContent() != null) {
            styleNodeTree(scroll.getContent());
        } else if (node instanceof TabPane tabs) {
            for (Tab tab : tabs.getTabs()) {
                if (tab.getContent() != null) styleNodeTree(tab.getContent());
            }
        }
    }

    private void styleTextField(TextField field) {
        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9px;" +
                        "-fx-background-radius: 9px;" +
                        "-fx-padding: 9px 10px 9px 10px;" +
                        "-fx-text-fill: " + TEXT + ";"
        );
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9px;" +
                        "-fx-background-radius: 9px;"
        );
    }

    private void stylePrimaryButton(Button button) {
        button.setStyle(
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10px 18px 10px 18px;" +
                        "-fx-cursor: hand;"
        );
    }

    private void styleCard(VBox card) {
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14px;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 14px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(11,39,66,0.09), 18, 0.10, 0, 5);"
        );
    }

    private Label pageTitle(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + NAVY + ";"
        );
        return label;
    }

    private Label pageDescription(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + MUTED + ";");
        return label;
    }

    private Label cardHeading(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: " + NAVY + ";"
        );
        return label;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 10.5px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";"
        );
        return label;
    }

    private Separator divider() {
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + BORDER + ";");
        return separator;
    }

    private VBox compactInput(String labelText, TextField input) {
        input.setPrefHeight(32);
        input.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(4, sectionLabel(labelText.toUpperCase(Locale.US)), input);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private Label educationParagraph(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + TEXT + "; -fx-font-size: 11.5px;");
        return label;
    }

    private VBox inputSection(String labelText, TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefHeight(35);
        field.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(5, sectionLabel(labelText.toUpperCase(Locale.US)), field);
        GridPane.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private GridPane resultGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(185);
        labels.setPrefWidth(210);
        ColumnConstraints values = new ColumnConstraints();
        values.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labels, values);
        return grid;
    }

    private ColumnConstraints flexibleColumn() {
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(33.333);
        c.setHgrow(Priority.ALWAYS);
        return c;
    }

    private static Label outputLabel() {
        Label label = new Label("-");
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #003057;");
        return label;
    }

    private static Label wrappedOutputLabel() {
        Label label = outputLabel();
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private HBox createIncomeScale() {
        Label min = new Label("$100K");
        Label middle = new Label("$1M");
        Label max = new Label("$5M+");
        for (Label label : List.of(min, middle, max)) {
            label.setStyle("-fx-font-size: 10px; -fx-text-fill: " + MUTED + ";");
        }
        Region one = new Region();
        Region two = new Region();
        HBox.setHgrow(one, Priority.ALWAYS);
        HBox.setHgrow(two, Priority.ALWAYS);
        return new HBox(min, one, middle, two, max);
    }

    private HBox allocationRow(String name, ProgressBar bar, Label value) {
        Label label = new Label(name);
        label.setMinWidth(55);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(16);
        HBox.setHgrow(bar, Priority.ALWAYS);
        value.setMinWidth(40);
        HBox row = new HBox(9, label, bar, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void addResultRow(GridPane grid, int row, String title, Label value) {
        Label label = new Label(title + ":");
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + ";");
        value.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row);
        grid.add(value, 1, row);
        GridPane.setHgrow(value, Priority.ALWAYS);
    }

    // ============================================================
    // FORMATTERS / VALIDATION
    // ============================================================

    private TextFormatter<String> integerFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0," + maxLength + "}") ? change : null
        );
    }

    private TextFormatter<String> decimalFormatter(int integerDigits, int decimalDigits) {
        return new TextFormatter<>(change -> {
            String pattern = "\\d{0," + integerDigits + "}(\\.\\d{0," + decimalDigits + "})?";
            return change.getControlNewText().matches(pattern) ? change : null;
        });
    }

    private TextFormatter<String> signedDecimalFormatter(int integerDigits, int decimalDigits) {
        return new TextFormatter<>(change -> {
            String pattern = "-?\\d{0," + integerDigits + "}(\\.\\d{0," + decimalDigits + "})?";
            return change.getControlNewText().matches(pattern) ? change : null;
        });
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank() || value.equals("-") || value.equals(".")) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateIncomeDisplay() {
        int position = (int) Math.round(incomeSlider.getValue());
        long income = INCOME_LEVELS[position];
        incomeDisplay.setText(position == INCOME_LEVELS.length - 1
                ? "$5,000,000+"
                : currencyFormatter.format(income));
        incomeDisplay.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: " + GOLD + ";"
        );
    }

    private String formatFundDisclosure(Fund fund, int allocationPercentage) {
        return allocationPercentage + "% — " + fund.getName() +
                "\nBenchmark: " + fund.getTicker() +
                " | Illustrative expense ratio: " + formatPercentage(fund.getExpenseRatio()) +
                " | Passive benchmark exposure";
    }

    private String formatPercentage(double decimalValue) {
        return String.format(Locale.US, "%.2f%%", decimalValue * 100.0);
    }

    private String formatAllocationPercent(double percentageValue) {
        return String.format(Locale.US, "%.2f%%", percentageValue);
    }

    private int roundToNearestFive(int year) {
        return (int) Math.round(year / 5.0) * 5;
    }

    private void showBuilderError(String message) {
        builderErrorLabel.setText(message);
        builderErrorLabel.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: " + ERROR + "; -fx-font-weight: bold;"
        );
    }

    private void clearBuilderError() {
        builderErrorLabel.setText("");
    }

    // ============================================================
    // ENUMS
    // ============================================================

    private enum AssetClass {
        STOCKS("Stocks"), BONDS("Bonds"), CASH("Cash");
        private final String displayName;
        AssetClass(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private enum GlidePathType {
        TO_RETIREMENT("To retirement"),
        THROUGH_RETIREMENT("Through retirement");
        private final String displayName;
        GlidePathType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
        @Override public String toString() { return displayName; }
    }

    private enum PortfolioStyle {
        SIMPLE("Simple — Broad Market"),
        DIVERSIFIED("Diversified — Recommended"),
        ADVANCED("Advanced — More Detailed");

        private final String displayName;
        PortfolioStyle(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
        @Override public String toString() { return displayName; }
    }

    // ============================================================
    // DATA CLASSES
    // ============================================================

    private static class Fund {
        private final String name;
        private final String ticker;
        private final AssetClass assetClass;
        private final double expenseRatio;
        private final boolean indexFund;
        private final String sponsor;
        private final boolean proprietary;

        private Fund(
                String name,
                String ticker,
                AssetClass assetClass,
                double expenseRatio,
                boolean indexFund,
                String sponsor,
                boolean proprietary
        ) {
            this.name = name;
            this.ticker = ticker;
            this.assetClass = assetClass;
            this.expenseRatio = expenseRatio;
            this.indexFund = indexFund;
            this.sponsor = sponsor;
            this.proprietary = proprietary;
        }

        public String getName() { return name; }
        public String getTicker() { return ticker; }
        public AssetClass getAssetClass() { return assetClass; }
        public double getExpenseRatio() { return expenseRatio; }
        public boolean isIndexFund() { return indexFund; }
        public String getSponsor() { return sponsor; }
        public boolean isProprietary() { return proprietary; }
    }

    private static class Allocation {
        private final int stocks;
        private final int bonds;
        private final int cash;

        private Allocation(int stocks, int bonds, int cash) {
            if (stocks + bonds + cash != 100) {
                throw new IllegalArgumentException("Allocation percentages must total 100.");
            }
            this.stocks = stocks;
            this.bonds = bonds;
            this.cash = cash;
        }

        public int getStocks() { return stocks; }
        public int getBonds() { return bonds; }
        public int getCash() { return cash; }
    }

    private static class ProjectionPoint {
        private final int year;
        private final double balance;
        private ProjectionPoint(int year, double balance) {
            this.year = year;
            this.balance = balance;
        }
        public int getYear() { return year; }
        public double getBalance() { return balance; }
    }

    private static class PortfolioImplementationRow {
        private final String category;
        private final String assetClass;
        private final double portfolioPercentage;
        private final double dollarTarget;
        private final String purpose;

        private PortfolioImplementationRow(
                String category,
                String assetClass,
                double portfolioPercentage,
                double dollarTarget,
                String purpose
        ) {
            this.category = category;
            this.assetClass = assetClass;
            this.portfolioPercentage = portfolioPercentage;
            this.dollarTarget = dollarTarget;
            this.purpose = purpose;
        }

        public String getCategory() { return category; }
        public String getAssetClass() { return assetClass; }
        public double getPortfolioPercentage() { return portfolioPercentage; }
        public double getDollarTarget() { return dollarTarget; }
        public String getPurpose() { return purpose; }
    }

    private static class AnnualAllocationRow {
        private final int year;
        private final int age;
        private final int yearsToRetirement;
        private final int stocks;
        private final int bonds;
        private final int cash;
        private final double expectedReturn;
        private final double projectedBalance;
        private final double annualContribution;
        private final double stockDollars;
        private final double bondDollars;
        private final double cashDollars;

        private AnnualAllocationRow(
                int year,
                int age,
                int yearsToRetirement,
                Allocation allocation,
                double expectedReturn,
                double projectedBalance,
                double annualContribution
        ) {
            this.year = year;
            this.age = age;
            this.yearsToRetirement = yearsToRetirement;
            this.stocks = allocation.getStocks();
            this.bonds = allocation.getBonds();
            this.cash = allocation.getCash();
            this.expectedReturn = expectedReturn;
            this.projectedBalance = projectedBalance;
            this.annualContribution = annualContribution;
            this.stockDollars = projectedBalance * stocks / 100.0;
            this.bondDollars = projectedBalance * bonds / 100.0;
            this.cashDollars = projectedBalance * cash / 100.0;
        }

        public int getYear() { return year; }
        public int getAge() { return age; }
        public int getYearsToRetirement() { return yearsToRetirement; }
        public int getStocks() { return stocks; }
        public int getBonds() { return bonds; }
        public int getCash() { return cash; }
        public double getExpectedReturn() { return expectedReturn; }
        public double getProjectedBalance() { return projectedBalance; }
        public double getAnnualContribution() { return annualContribution; }
        public double getStockDollars() { return stockDollars; }
        public double getBondDollars() { return bondDollars; }
        public double getCashDollars() { return cashDollars; }
    }

    private static class MonteCarloResult {
        private final double p10;
        private final double median;
        private final double p90;
        private final double goalProbability;

        private MonteCarloResult(double p10, double median, double p90, double goalProbability) {
            this.p10 = p10;
            this.median = median;
            this.p90 = p90;
            this.goalProbability = goalProbability;
        }

        public double getP10() { return p10; }
        public double getMedian() { return median; }
        public double getP90() { return p90; }
        public double getGoalProbability() { return goalProbability; }
    }

    private static class ProjectionResult {
        private final List<ProjectionPoint> points;
        private final List<AnnualAllocationRow> annualRows;
        private final double endingBalance;
        private final double totalPrincipal;
        private final double investmentGrowth;

        private ProjectionResult(
                List<ProjectionPoint> points,
                List<AnnualAllocationRow> annualRows,
                double endingBalance,
                double totalPrincipal,
                double investmentGrowth
        ) {
            this.points = points;
            this.annualRows = annualRows;
            this.endingBalance = endingBalance;
            this.totalPrincipal = totalPrincipal;
            this.investmentGrowth = investmentGrowth;
        }

        public List<ProjectionPoint> getPoints() { return points; }
        public List<AnnualAllocationRow> getAnnualRows() { return annualRows; }
        public double getEndingBalance() { return endingBalance; }
        public double getTotalPrincipal() { return totalPrincipal; }
        public double getInvestmentGrowth() { return investmentGrowth; }
    }

    private static class CurrencyAxisFormatter extends NumberAxis.DefaultFormatter {
        private CurrencyAxisFormatter(NumberAxis axis) { super(axis); }

        @Override
        public String toString(Number value) {
            double amount = value.doubleValue();
            if (Math.abs(amount) >= 1_000_000_000) {
                return String.format(Locale.US, "$%.1fB", amount / 1_000_000_000);
            }
            if (Math.abs(amount) >= 1_000_000) {
                return String.format(Locale.US, "$%.1fM", amount / 1_000_000);
            }
            if (Math.abs(amount) >= 1_000) {
                return String.format(Locale.US, "$%.0fK", amount / 1_000);
            }
            return String.format(Locale.US, "$%.0f", amount);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
