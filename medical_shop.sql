-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 25, 2024 at 12:32 PM
-- Server version: 5.6.16
-- PHP Version: 5.5.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `medical_shop`
--

-- --------------------------------------------------------

--
-- Table structure for table `medicines`
--

CREATE TABLE IF NOT EXISTS `medicines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `expiration_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

--
-- Dumping data for table `medicines`
--

INSERT INTO `medicines` (`id`, `name`, `price`, `expiration_date`) VALUES
(2, 'Paracetamol', 399, '2026-12-31'),
(3, 'Ibuprofen', 499, '2025-10-30'),
(4, 'Amoxicillin', 859, '2024-10-15'),
(5, 'Omeprazole', 659, '2027-09-15'),
(6, 'Aspirin', 269, '2026-12-05'),
(8, 'Cetirizine', 149, '2027-11-20'),
(9, 'Ranitidine', 229, '2026-03-07'),
(11, 'Dolo', 23, '2026-09-05');

-- --------------------------------------------------------

--
-- Table structure for table `sold_medicines`
--

CREATE TABLE IF NOT EXISTS `sold_medicines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `sold_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `sold_medicines`
--

INSERT INTO `sold_medicines` (`id`, `name`, `price`, `sold_date`) VALUES
(1, 'dolo', 52, '2024-02-24'),
(2, 'dollo', 25, '2024-02-24'),
(3, 'Metformin', 179, '2024-02-25');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
