-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 28, 2026 at 04:53 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `beansense`
--

-- --------------------------------------------------------

--
-- Table structure for table `sensor_berat`
--

CREATE TABLE `sensor_berat` (
  `id` bigint(20) NOT NULL,
  `berat` double NOT NULL,
  `satuan` varchar(10) NOT NULL DEFAULT 'gram',
  `timestamp` datetime(6) NOT NULL,
  `wadah` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sensor_berat`
--

INSERT INTO `sensor_berat` (`id`, `berat`, `satuan`, `timestamp`, `wadah`) VALUES
(1, 0.05, 'gram', '2026-06-06 14:24:31.000000', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `sensor_warna`
--

CREATE TABLE `sensor_warna` (
  `id` bigint(20) NOT NULL,
  `hasil_klasifikasi` varchar(50) NOT NULL,
  `sensor_warna` varchar(50) NOT NULL,
  `timestamp` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sensor_warna`
--

INSERT INTO `sensor_warna` (`id`, `hasil_klasifikasi`, `sensor_warna`, `timestamp`) VALUES
(1,  'MERAH',  'MERAH', '2026-05-12 23:57:28.000000'),
(2,  'MERAH',  'MERAH', '2026-05-13 00:51:41.000000'),
(3,  'MERAH',  'MERAH', '2026-05-13 01:20:45.000000'),
(4,  'MERAH',  'MERAH', '2026-05-17 13:40:56.000000'),
(5,  'HIJAU',  'HIJAU', '2026-05-17 13:41:50.000000'),
(6,  'MERAH',  'MERAH', '2026-05-17 13:45:08.000000'),
(7,  'MATANG', 'MERAH', '2026-06-04 05:55:36.000000'),
(8,  'MATANG', 'MERAH', '2026-06-04 05:56:34.000000'),
(9,  'MATANG', 'MERAH', '2026-06-04 05:57:23.000000'),
(10, 'MATANG', 'MERAH', '2026-06-04 05:59:46.000000'),
(11, 'MATANG', 'MERAH', '2026-06-04 05:59:51.000000'),
(12, 'MATANG', 'MERAH', '2026-06-04 06:00:00.000000'),
(13, 'MATANG', 'MERAH', '2026-06-04 06:00:03.000000'),
(14, 'MATANG', 'MERAH', '2026-06-06 14:24:27.000000'),
(15, 'MATANG', 'MERAH', '2026-06-06 14:24:58.000000'),
(16, 'MATANG', 'MERAH', '2026-06-06 14:25:31.000000'),
(17, 'MATANG', 'MERAH', '2026-06-17 09:08:04.000000'),
(18, 'MENTAH', 'HIJAU', '2026-06-17 09:08:40.000000');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL DEFAULT '',
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','OPERATOR') NOT NULL,
  `status` enum('aktif','suspend') DEFAULT 'aktif',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `nama`, `username`, `email`, `password`, `role`, `status`, `created_at`) VALUES
(1, 'Administrator', 'admin', 'admin@beansense.com', '$2a$10$w3.xYPQhRqzJ/lHxzemCDOE9Rbb7I6h/L3U3EFk6g23v.NLUPZhya', 'ADMIN', 'aktif', '2026-06-15 14:09:09'),
(3, 'Noall', 'noall', 'noall@gmail.com', '$2a$10$.4xKiTvqxMjtqO9SFUbQ/u6eev62NQJZToBYEO6vdvCvaoOXlVlDa', 'OPERATOR', 'aktif', '2026-06-15 14:26:43'),
(4, 'Fachri', 'ari', 'ari@gmail.com', '$2a$10$erCwwzEhK/G8GUklJzQtJuhWTivtCXNJgJi/e6ynY0myCG1Np2IZS', 'OPERATOR', 'aktif', '2026-06-17 01:59:33'),
(5, 'fOrsaken', 'jason', 'forsaken@gmail.com', '$2a$10$BwIQ1mVo9ju.IiDDaaFI3eVmQyNDWWC6pLhrrmOhZB.hXdYOO7yf2', 'ADMIN', 'aktif', '2026-06-20 06:38:08'),
(6, 'Junzi Fajri', 'fajri', 'fajri@gmail.com', '$2a$10$lrov27mPCgwFgZjWW8VcT.PAi268YROXOcYWdF5rEDK97EcZzPF7q', 'OPERATOR', 'aktif', '2026-06-27 08:56:14');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `sensor_berat`
--
ALTER TABLE `sensor_berat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_sensor_data_timestamp` (`timestamp`);

--
-- Indexes for table `sensor_warna`
--
ALTER TABLE `sensor_warna`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_sensor_log_timestamp` (`timestamp`),
  ADD KEY `idx_sensor_log_warna` (`sensor_warna`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `sensor_berat`
--
ALTER TABLE `sensor_berat`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `sensor_warna`
--
ALTER TABLE `sensor_warna`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
