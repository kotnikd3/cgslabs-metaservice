-- Models
INSERT INTO model (name) VALUES ('219:0:Inca60');
INSERT INTO model (name) VALUES ('SI:CGS:Metro01');
INSERT INTO model (name) VALUES ('219:0:Aladin');
INSERT INTO model (name) VALUES ('219:0:Inca30');

-- Station
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Kranj delavski most', 'DRSI:Grosuplje:Miha Novak', 46.2286, 14.3632, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Kranj 2', 'DRSI:Grosuplje:Miha Novak', 46.228601, 14.363201, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Jeprca', 'DRSI:Grosuplje:Miha Novak', 46.1841, 14.3792, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Trojane', 'DRSI:Grosuplje:Miha Novak', 46.206, 14.9005, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Vahta', 'DRSI:Grosuplje:Miha Novak', 45.7308, 15.2294, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Lenart', 'DRSI:Grosuplje:Miha Novak', 46.5999, 15.8569, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Rimske Toplice', 'DRSI:Grosuplje:Miha Novak', 46.1214, 15.2023, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Črnova', 'DRSI:Grosuplje:Miha Novak', 46.3141, 15.1737, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Črni Vrh', 'DRSI:Grosuplje:Miha Novak', 45.9142, 14.0322, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Ravbarkomanda', 'DRSI:Grosuplje:Miha Novak', 45.7997, 14.2366, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Hrušica', 'DRSI:Grosuplje:Miha Novak', 46.4513, 13.9918, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Soteska', 'DRSI:Grosuplje:Miha Novak', 46.3115, 14.0633, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Dolsko', 'DRSI:Grosuplje:Miha Novak', 46.0957, 14.6798, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Vrhnika', 'DRSI:Grosuplje:Miha Novak', 45.9614, 14.2869, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Radlje', 'DRSI:Grosuplje:Miha Novak', 46.6141, 15.2017, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Mislinja', 'DRSI:Grosuplje:Miha Novak', 46.4356, 15.1901, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Ihova', 'DRSI:Grosuplje:Miha Novak', 46.6633, 15.93, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Trebnje', 'DRSI:Grosuplje:Miha Novak', 45.9166, 15.0387, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Črmošnjice', 'DRSI:Grosuplje:Miha Novak', 45.671, 15.102, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Trojane - most', 'DRSI:Grosuplje:Miha Novak', 46.206001, 14.900501, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Predel', 'DRSI:Grosuplje:Miha Novak', 46.4219, 13.5955, 350);
INSERT INTO station (name, customer_id, latitude, longitude, elevation) VALUES ('Predel - most', 'DRSI:Grosuplje:Miha Novak', 46.421901, 13.595501, 350);

-- Base can see station
INSERT INTO base_can_see_station (base_id, station_id) VALUES (1, 1);


-- Measured parameters
INSERT INTO measured_parameter (name) VALUES ('Air temperature in [deg. C] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Road temperature in [deg. C] @ Ground surface');
INSERT INTO measured_parameter (name) VALUES ('Road temperature in [deg. C] @ 25mm below ground surface');
INSERT INTO measured_parameter (name) VALUES ('Road temperature in [deg. C] @ 50mm below ground surface');
INSERT INTO measured_parameter (name) VALUES ('Road temperature in [deg. C] @ 300mm below ground surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro freezing point temperature MgCl2 in [deg. C] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro freezing point temperature CaCl2 in [deg. C] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Dew point temperature in [deg. C] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Air temperature in [deg. C] @ Inside the road weather station');
INSERT INTO measured_parameter (name) VALUES ('Air humidity in [%] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Liquid water film thickness in [mm] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Precipitation type Thies Clima US4920 [categorical] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Precipitation intensity in [mm h^-1] @ Ground surface');
INSERT INTO measured_parameter (name) VALUES ('Wind speed in [m s^-1] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Wind gusts speed in [m s^-1] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Wind direction in [arc degree] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Air pressure in [hPa] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Snow depth in [cm] @ Ground surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31pro road condition in [categorical] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Visibility by WMO standard in [m] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Weather station supply voltage in [V]');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro - CaCl2 saline concentration in [%] @ Road surface');
INSERT INTO measured_parameter (name) VALUES ('10 minutes precipitation accumulation in [mm] @ Ground surface');
INSERT INTO measured_parameter (name) VALUES ('Wind gust direction in [arc degree] @ Road weather station''s height');
INSERT INTO measured_parameter (name) VALUES ('Lufft IRS31 Pro sensor state of coupling [categorical]');
INSERT INTO measured_parameter (name) VALUES ('24 hours precipitation accumulation in [mm] @ Ground surface');

-- Forecasted parameters
INSERT INTO forecasted_parameter (name) VALUES ('Temperature in [deg. C] @ 150cm above ground');
INSERT INTO forecasted_parameter (name) VALUES ('Temperature in [deg. C] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Dew point temperature in [deg. C] @ 150cm above ground');
INSERT INTO forecasted_parameter (name) VALUES ('Relative air humidity [%] @ 200cm above ground');
INSERT INTO forecasted_parameter (name) VALUES ('Wind in [km h^-1] @ 10m above ground');
INSERT INTO forecasted_parameter (name) VALUES ('Rain precipitation rate in [mm h^-1] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Snow precipitation rate [cm h^-1] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Total precipitation rate in [mm h^-1] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('INCA precipitation type in [categorical] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Pressure in [Pa] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Downwards infrared radiation flux in [W m^-2] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Downwards solar radiation flux in [W m^-2] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Downwards anthropogenic radiation flux in [W m^-2] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Total cloud cover in [%] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Octal cloud cover in [categorical] @ Ground or water surface');
INSERT INTO forecasted_parameter (name) VALUES ('Road temperature in [deg. C] @ Ground surface');
INSERT INTO forecasted_parameter (name) VALUES ('Road temperature in [deg. C] @ 400mm below ground surface');
INSERT INTO forecasted_parameter (name) VALUES ('Metro road condition in [categorical] @ Road surface');


-- Parameter on station
-- Kranj delavski most
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Air temperature in [deg. C] @ Inside the road weather station', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (1, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Kranj 2
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Air temperature in [deg. C] @ Inside the road weather station', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (2, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Jeprca
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (3, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Trojane
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (4, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Vahta
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (5, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Lenart
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Air temperature in [deg. C] @ Inside the road weather station', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (6, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Rimske Toplice
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (7, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Črnova
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (8, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Črni Vrh
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (9, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Ravbarkomanda
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (10, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');

-- Hrušica
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (11, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Soteska
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (12, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Dolsko
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (13, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Vrhnika
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (14, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Radlje
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (15, 'Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface', 'Left driving line');

-- Mislinja
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (16, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Ihova
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (17, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Trebnje
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (18, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Črmošnjice
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (19, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');

-- Trojane - most
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Road temperature in [deg. C] @ 25mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Visibility by WMO standard in [m] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (20, '10 minutes precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Predel
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Road temperature in [deg. C] @ 300mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Liquid water film thickness in [mm] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Air temperature in [deg. C] @ Inside the road weather station', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, 'Snow depth in [cm] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (21, '24 hours precipitation accumulation in [mm] @ Ground surface', 'Left driving line');

-- Predel - most
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Air temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Air humidity in [%] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Dew point temperature in [deg. C] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Road temperature in [deg. C] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Road temperature in [deg. C] @ 50mm below ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Lufft IRS31pro road condition in [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Lufft IRS31 Pro sensor state of coupling [categorical]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Precipitation intensity in [mm h^-1] @ Ground surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Precipitation type Thies Clima US4920 [categorical] @ Road surface', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Air pressure in [hPa] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Air temperature in [deg. C] @ Inside the road weather station', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Weather station supply voltage in [V]', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Wind speed in [m s^-1] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Wind direction in [arc degree] @ Road weather station''s height', 'Left driving line');
INSERT INTO parameter_on_station (station_id, measured_parameter_name, sensor_num) VALUES (22, 'Snow depth in [cm] @ Ground surface', 'Left driving line');


-- Location
INSERT INTO location (latitude, longitude) VALUES (46.2286, 14.3632);
INSERT INTO location (latitude, longitude) VALUES (46.228601, 14.363201);

-- Subscription
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Total cloud cover in [%] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards infrared radiation flux in [W m^-2] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Pressure in [Pa] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards solar radiation flux in [W m^-2] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Temperature in [deg. C] @ 150cm above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Dew point temperature in [deg. C] @ 150cm above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Temperature in [deg. C] @ Ground or water surface', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Wind in [km h^-1] @ 10m above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'INCA precipitation type in [categorical] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Rain precipitation rate in [mm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Snow precipitation rate [cm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Total precipitation rate in [mm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Road temperature in [deg. C] @ Ground surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Metro road condition in [categorical] @ Road surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Temperature in [deg. C] @ 150cm above ground', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Dew point temperature in [deg. C] @ 150cm above ground', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Wind in [km h^-1] @ 10m above ground', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Rain precipitation rate in [mm h^-1] @ Ground or water surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Snow precipitation rate [cm h^-1] @ Ground or water surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards infrared radiation flux in [W m^-2] @ Ground or water surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.2286, 14.3632, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards solar radiation flux in [W m^-2] @ Ground or water surface', 'SI:CGS:Metro01');

INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Total cloud cover in [%] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards infrared radiation flux in [W m^-2] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Pressure in [Pa] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Downwards solar radiation flux in [W m^-2] @ Ground or water surface', '219:0:Aladin');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Temperature in [deg. C] @ 150cm above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Dew point temperature in [deg. C] @ 150cm above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Temperature in [deg. C] @ Ground or water surface', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Wind in [km h^-1] @ 10m above ground', '219:0:Inca60');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'INCA precipitation type in [categorical] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Rain precipitation rate in [mm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Snow precipitation rate [cm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Total precipitation rate in [mm h^-1] @ Ground or water surface', '219:0:Inca30');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Road temperature in [deg. C] @ Ground surface', 'SI:CGS:Metro01');
INSERT INTO subscription (latitude, longitude, elevation, customer_id, forecasted_parameter_name, model_name) VALUES(46.228601, 14.363201, 380, 'DRSI:Grosuplje:Miha Novak', 'Metro road condition in [categorical] @ Road surface', 'SI:CGS:Metro01');

-- METRO config
INSERT INTO metro_config(latitude, longitude, model_name, config) VALUES (46.2286, 14.3632, 'SI:CGS:Metro01', '{"geoLocation":{"latitude":46.2286,"longitude":14.3632},"forecastModelId":"SI:CGS:Metro01","type":"bridge","subSurfaceSensorDepth":0.3,"roadLayers":[{"position":1,"type":"asphalt","thickness":0.02},{"position":2,"type":"cement","thickness":0.3}],"visibleHorizonDirections":[],"measurementsMappings":{"Metro observation <td> = Dew point temperature in [deg. C] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <at> = Air temperature in [deg. C] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Air temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <sst> = Road sub-surface temperature in [deg. C] @ sub-surface sensor depth below ground surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Air temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <pi> = Presence of precipitation in [0:No 1:Yes] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Precipitation intensity in [mm h^-1] @ Ground surface","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Total precipitation rate in [mm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca60"}],"Metro observation <ws> = Wind speed in [km h^-1] @ Road weather station''s height":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Wind in [km h^-1] @ 10m above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <sc> = Road condition in [33:Dry 34:Wet 35:Ice/Snow 40:Frost] @ Road surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Lufft IRS31pro road condition in [categorical] @ Road surface","dataSourceId":"1"}],"Metro observation <st> = Road temperature in [deg. C] @ Ground surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Road temperature in [deg. C] @ Ground surface","dataSourceId":"1"}]},"weatherForecastMappings":{"Metro weather forecast <at> = Temperature in [deg. C] @ 150cm above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <ws> = Wind in [km h^-1] @ 10m above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Wind in [km h^-1] @ 10m above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <sn> = Snow precipitation accumulation since beginning of the forecast in [cm] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Snow precipitation rate [cm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca30"}],"Metro weather forecast <ap> = Pressure in [mb] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Pressure in [Pa] @ Ground or water surface","dataSourceId":"219:0:Aladin"}],"Metro weather forecast <td> = Dew point temperature in [deg. C] @ 150cm above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <ra> = Rain precipitation accumulation since beginning of the forecast in [mm] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Rain precipitation rate in [mm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca30"}],"Metro weather forecast <sf> = Downwards solar radiation flux in [W m^-2] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Downwards solar radiation flux in [W m^-2] @ Ground or water surface","dataSourceId":"219:0:Aladin"}],"Metro weather forecast <ir> = Downwards infrared radiation flux in [W m^-2] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Downwards infrared radiation flux in [W m^-2] @ Ground or water surface","dataSourceId":"219:0:Aladin"}]},"solarFluxFactor":0.5,"infraredFluxFactor":1.05,"anthropogenicFluxFactor":10.0,"enableSunshadow":false,"sunshadowMethod":"2","useAnthropogenicFlux":false,"useInfraredFlux":true,"useSolarFlux":true,"verbosityLevel":"4"}');
INSERT INTO metro_config(latitude, longitude, model_name, config) VALUES (46.228601, 14.363201, 'SI:CGS:Metro01', '{"geoLocation":{"latitude":46.228601,"longitude":14.363201},"forecastModelId":"SI:CGS:Metro01","type":"bridge","subSurfaceSensorDepth":0.3,"roadLayers":[{"position":1,"type":"asphalt","thickness":0.02},{"position":2,"type":"cement","thickness":0.3}],"visibleHorizonDirections":[],"measurementsMappings":{"Metro observation <td> = Dew point temperature in [deg. C] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <at> = Air temperature in [deg. C] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Air temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <sst> = Road sub-surface temperature in [deg. C] @ sub-surface sensor depth below ground surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Air temperature in [deg. C] @ Road weather station''s height","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <pi> = Presence of precipitation in [0:No 1:Yes] @ Road weather station''s height":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Precipitation intensity in [mm h^-1] @ Ground surface","dataSourceId":"1"},{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Total precipitation rate in [mm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca60"}],"Metro observation <ws> = Wind speed in [km h^-1] @ Road weather station''s height":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Wind in [km h^-1] @ 10m above ground","dataSourceId":"219:0:Inca60"}],"Metro observation <sc> = Road condition in [33:Dry 34:Wet 35:Ice/Snow 40:Frost] @ Road surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Lufft IRS31pro road condition in [categorical] @ Road surface","dataSourceId":"1"}],"Metro observation <st> = Road temperature in [deg. C] @ Ground surface":[{"type":"MEASUREMENT","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Road temperature in [deg. C] @ Ground surface","dataSourceId":"1"}]},"weatherForecastMappings":{"Metro weather forecast <at> = Temperature in [deg. C] @ 150cm above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <ws> = Wind in [km h^-1] @ 10m above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Wind in [km h^-1] @ 10m above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <sn> = Snow precipitation accumulation since beginning of the forecast in [cm] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Snow precipitation rate [cm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca30"}],"Metro weather forecast <ap> = Pressure in [mb] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Pressure in [Pa] @ Ground or water surface","dataSourceId":"219:0:Aladin"}],"Metro weather forecast <td> = Dew point temperature in [deg. C] @ 150cm above ground":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Dew point temperature in [deg. C] @ 150cm above ground","dataSourceId":"219:0:Inca60"}],"Metro weather forecast <ra> = Rain precipitation accumulation since beginning of the forecast in [mm] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Rain precipitation rate in [mm h^-1] @ Ground or water surface","dataSourceId":"219:0:Inca30"}],"Metro weather forecast <sf> = Downwards solar radiation flux in [W m^-2] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Downwards solar radiation flux in [W m^-2] @ Ground or water surface","dataSourceId":"219:0:Aladin"}],"Metro weather forecast <ir> = Downwards infrared radiation flux in [W m^-2] @ Ground or water surface":[{"type":"FORECAST","geographicLocation":{"latitude":46.2286,"longitude":14.3632},"parameterLabel":"Downwards infrared radiation flux in [W m^-2] @ Ground or water surface","dataSourceId":"219:0:Aladin"}]},"solarFluxFactor":0.5,"infraredFluxFactor":1.05,"anthropogenicFluxFactor":10.0,"enableSunshadow":false,"sunshadowMethod":"2","useAnthropogenicFlux":false,"useInfraredFlux":true,"useSolarFlux":true,"verbosityLevel":"4"}');

