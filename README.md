# java-gen-ai-course

## Lab 1:

- Temperature: 0.1:
  <img src="img/lab_01_temperature_1_0.png" alt="Temperature 1.0">

- Temperature: 0.2 (may occur some hallucinations):
  <img src="img/lab_01_temperature_1_0.png" alt="Temperature 2.0">

---

## Lab 2:

I`ve done some tests to check chat memory:

1. Greet to llm using `test-1` as user-id:

   <img src="img/lab_02_name_test_1.png" alt="Max presenting">

2. Greet to llm using `test-2` as user-id:

   <img src="img/lab_02_name_test_2.png" alt="Tony presenting">

3. Ask llm about my name using `test-1` user-id:

   <img src="img/lab_02_name_test_3.png" alt="Max asking">

4. Ask llm about my name using `test-2` user-id:

   <img src="img/lab_02_name_test_4.png" alt="Tony asking">

---

## Lab 3:

1. Call API with `gpt-35-turbo`:

   <img src="img/lab_03_model_gpt_35_turbo.png" alt="gpt-35-turbo">

2. Call API with `gpt-4-0613`:

   <img src="img/lab_03_model_gpt_4_0613.png" alt="gpt-4-0613">

---

## Lab 4:

Plugins:

1. Call `AgeCalculatorPlugin`:

   <img src="img/lab_04_calculate_age_plugin.png" alt="AgeCalculatorPlugin">

2. Call `WeatherForecastPlugin`:

   <img src="img/lab_04_weather_forecast_plugin.png" alt="WeatherForecastPlugin">

3. Call `CurrencyConverterPlugin`:

   <img src="img/lab_04_currency_converter_plugin.png" alt="CurrencyConverterPlugin">

- Logs output:

  <img src="img/lab_04_logs_output.png" alt="Logs">

## Lab 5:

1. Call `build` endpoint to get embeddings for `text` field:

  <img src="img/lab_05_build_endpoint.png" alt="Logs">

2. Call `build and store` endpoint to get embedding and store result to vector DB:

  <img src="img/lab_05_build_and_store.png" alt="Logs">

3. Call `search` endpoint to search similarity by input `text` field:

  <img src="img/lab_05_search_endpoint.png" alt="Logs">

## Lab 6:

1. Upload to context [christmasday.pdf](docs/christmasday.pdf):

<img src="img/lab_06_upload_context.png" alt="Upload context">

2. Check qdrant collections:

<img src="img/lab_06_qdrant_points.png" alt="qdrant">

3. Ask question from context:

<img src="img/lab_06_with_context.png" alt="With context">

4. Ask not related to context question:

<img src="img/lab_06_no_context.png" alt="Without context">
