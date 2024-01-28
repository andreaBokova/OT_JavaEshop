-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: So 10.Dec 2022, 21:23
-- Verzia serveru: 10.4.24-MariaDB
-- Verzia PHP: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `eshop`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `kosik`
--

CREATE TABLE `kosik` (
  `ID` int(11) NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_polozky`
--

CREATE TABLE `obj_polozky` (
  `ID` int(11) NOT NULL,
  `ID_objednavky` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Sťahujem dáta pre tabuľku `obj_polozky`
--

INSERT INTO `obj_polozky` (`ID`, `ID_objednavky`, `ID_tovaru`, `cena`, `ks`) VALUES
(116, 1, 4, 66, 1),
(117, 2, 5, 90, 1),
(118, 3, 1, 58, 1),
(122, 5, 5, 90, 1);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_zoznam`
--

CREATE TABLE `obj_zoznam` (
  `ID` int(11) NOT NULL,
  `obj_cislo` int(11) NOT NULL,
  `datum_objednavky` date NOT NULL DEFAULT current_timestamp(),
  `ID_pouzivatela` int(11) NOT NULL,
  `stav` varchar(20) NOT NULL DEFAULT 'spracovaná',
  `suma` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Sťahujem dáta pre tabuľku `obj_zoznam`
--

INSERT INTO `obj_zoznam` (`ID`, `obj_cislo`, `datum_objednavky`, `ID_pouzivatela`, `stav`, `suma`) VALUES
(69, 1, '2022-12-10', 1, 'zaplatená', 66),
(70, 2, '2022-12-10', 1, 'odoslaná', 90),
(71, 3, '2022-12-10', 6, 'zaplatená', 58),
(73, 4, '2022-12-10', 1, 'spracovaná', 88);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `sklad`
--

CREATE TABLE `sklad` (
  `ID` int(11) NOT NULL,
  `nazov` varchar(60) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `ks` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `URL_obr` varchar(500) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `sklad`
--

INSERT INTO `sklad` (`ID`, `nazov`, `ks`, `cena`, `URL_obr`) VALUES
(1, 'Dior Poison Girl (W) 50ml', 4, 58, 'https://parfumeria-orion.sk/images_upd/products/7/qitrj9c15xnp.jpg '),
(2, 'Chloe Chloe (W) 50ml', 5, 60, 'https://parfumeria-orion.sk/images_upd/products/4/xz01fmuqe763.jpg '),
(3, 'Cartier La Panthere (W) 75ml', 5, 111, 'https://parfumeria-orion.sk/images_upd/products/6/82wj1cgb47zs.jpg '),
(4, 'Ralph Lauren Deep Blue Polo (M) 125ml', 4, 83, 'https://parfumeria-orion.sk/images_upd/products/6/29wbv3camx01.jpg\r\n\r\n'),
(5, 'Christian Dior Sauvage (M) 60ml', 4, 113, 'https://parfumeria-orion.sk/images_upd/products/4/ci1oquvpehkt.jpg  '),
(6, 'Chanel Allure Homme Sport (M) 100ml', 5, 110, 'https://parfumeria-orion.sk/images_upd/products/8/51ti3k6lnrzy.webp  \r\n');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `users`
--

CREATE TABLE `users` (
  `ID` int(11) NOT NULL,
  `login` varchar(30) NOT NULL,
  `passwd` varchar(10) NOT NULL,
  `adresa` varchar(50) NOT NULL,
  `zlava` int(11) NOT NULL DEFAULT 0,
  `meno` varchar(15) NOT NULL,
  `priezvisko` varchar(20) NOT NULL,
  `poznamky` text DEFAULT NULL,
  `je_admin` tinyint(4) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Sťahujem dáta pre tabuľku `users`
--

INSERT INTO `users` (`ID`, `login`, `passwd`, `adresa`, `zlava`, `meno`, `priezvisko`, `poznamky`, `je_admin`) VALUES
(1, 'jskalka@ukf.sk', '123', 'Zeleninova 4, Nitra', 20, 'Jan', 'Skalka', 'tester', 0),
(2, 'jmrkva@ukf.sk', '123', 'Zahrada 11', 3, 'Jozef', 'Mrkva', 'admin', 1),
(5, 'apekna@ukf.sk', '123', 'Napervillska 5, Nitra', 0, 'Anna', 'Pekna', 'admin2', 1),
(6, 'jmak@ukf.sk', '123', 'Napervillska 6, Nitra', 0, 'Jozef', 'Mak', 'tester2', 0);

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=123;

--
-- AUTO_INCREMENT pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;

--
-- AUTO_INCREMENT pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pre tabuľku `users`
--
ALTER TABLE `users`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
