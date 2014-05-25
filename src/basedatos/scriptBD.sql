/*
Created		14/01/2009
Modified		13/07/2009
Project		
Model		
Company		
Author		
Version		
Database		mySQL 5 
*/


DROP DATABASE IF EXISTS simab;
CREATE DATABASE simab;

USE simab;


drop table IF EXISTS analisisTecnico;
drop table IF EXISTS saldoXagente;
drop table IF EXISTS cotizacion;
drop table IF EXISTS accionesXagente;
drop table IF EXISTS producto;
drop table IF EXISTS bursatil;
drop table IF EXISTS ofertaVenta;
drop table IF EXISTS ofertaCompra;
drop table IF EXISTS calce;




Create table calce (
	IDCalce Int NOT NULL,
	IDOfertaCompra Int NOT NULL,
	IDOfertaVenta Int NOT NULL,
	IDProducto Int NOT NULL,
	precioCompra Float NOT NULL,
	precioVenta Float NOT NULL,
	fecha Int NOT NULL,
	cantidad Int NOT NULL,
	UNIQUE (IDCalce),
 Primary Key (IDCalce),
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade,
 Foreign Key (IDOfertaCompra) references ofertaCompra (IDOfertaCompra) on delete cascade on update cascade,
 Foreign Key (IDOfertaVenta) references ofertaVenta (IDOfertaVenta) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table ofertaCompra (
	IDOfertaCompra Int NOT NULL,
	IDAgente Int NOT NULL,
	IDProducto Int NOT NULL,
	precioCompra Float NOT NULL,
	fecha Int NOT NULL,
	cantidad Int NOT NULL,
	UNIQUE (IDOfertaCompra),
 Primary Key (IDOfertaCompra),
 Foreign Key (IDAgente) references bursatil (IDAgente) on delete cascade on update cascade,
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table ofertaVenta (
	IDOfertaVenta Int NOT NULL,
	IDAgente Int NOT NULL,
	IDProducto Int NOT NULL,
	precioVenta Float NOT NULL,
	fecha Int NOT NULL,
	cantidad Int NOT NULL,
	UNIQUE (IDOfertaVenta),
 Primary Key (IDOfertaVenta),
 Foreign Key (IDAgente) references bursatil (IDAgente) on delete cascade on update cascade,
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table bursatil (
	IDAgente Int NOT NULL,
	nombre Varchar(20) NOT NULL,
	tipo Int NOT NULL,
 Primary Key (IDAgente)
) ENGINE = MyISAM;

Create table producto (
	IDProducto Int NOT NULL,
	nombre Varchar(20) NOT NULL,
	descripcion Varchar(100),
	paqueteMinimo Int NOT NULL,
	UNIQUE (IDProducto),
 Primary Key (IDProducto)
) ENGINE = MyISAM;

Create table accionesXagente (
	IDAgente Int NOT NULL,
	IDProducto Int NOT NULL,
	fecha Int NOT NULL,
	cantidad Int NOT NULL,
 Primary Key (IDAgente,IDProducto,fecha),
 Foreign Key (IDAgente) references bursatil (IDAgente) on delete cascade on update cascade,
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table cotizacion (
	IDProducto Int NOT NULL,
	fecha Int NOT NULL,
	precioInicio Float,
	precioCierre Float,
	precioMax Float,
	precioMin Float,
	precioPromedio Float,
	volumen Int,
 Primary Key (IDProducto,fecha),
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table saldoXagente (
	IDAgente Int NOT NULL,
	fecha Int NOT NULL,
	saldo Float NOT NULL,
 Primary Key (IDAgente,fecha),
 Foreign Key (IDAgente) references bursatil (IDAgente) on delete cascade on update cascade
) ENGINE = MyISAM;

Create table analisisTecnico (
	IDAgente Int NOT NULL,
	IDProducto Int NOT NULL,
	fecha Int NOT NULL,
	indicador Varchar(20) NOT NULL,
	valor Float NOT NULL,
 Primary Key (IDAgente,IDProducto,fecha,indicador),
 Foreign Key (IDAgente) references bursatil (IDAgente) on delete cascade on update cascade,
 Foreign Key (IDProducto) references producto (IDProducto) on delete cascade on update cascade
) ENGINE = MyISAM;












