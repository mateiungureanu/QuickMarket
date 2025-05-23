# QuickMarket
## Programare Avansata pe Obiecte in Java (2024-2025)

## Table of contents
+ [Description](#description)
+ [Versions](#versions)
+ [Requirements](#requirements)
+ [Setup](#setup)
+ [Pictures](#pictures)

### Description
+ Full-stack web application with user authentication, shopping list, role-based access control, and actions. Hosted on Render / Railway. Data is stored in PostgreSQL.
+ It offers customers the option to choose a product and add it to their shopping list, leave reviews and report price discrepancies, and allows sellers to add and delete products, and update product quantities and prices. Admins can expand the network by adding a new market and can reinforce rules by banning consumers who review-bomb and suspending sellers who display a different price.
+ The purpose of the project is saving time when going for grocery shopping, essentially skipping the pre-buy phase of looking around for available products, comparing the prices and quality of the products when entering the market.
+ The target group is the busy people that get off their jobs late and do not have the time or energy to do a proper market check; however, thanks to this project they can prepare a shopping list on their phones in the public transport en route to the market.
+ The second target group is the organized people that want to prepare their shopping list at home and finish the shopping fast.

> **Note:** 
> This project is a work in progress and is not yet complete. The current version is a local application with a command line interface and no database. Future versions will include a web interface and database integration.

### Versions
+ Local application, command line interface, no database \(go to [branch](https://github.com/mateiungureanu/QuickMarket/tree/local_app_no_database)\)

[//]: # (+ Local application, command line interface, with database \&#40;go to [branch]&#40;https://github.com/mateiungureanu/QuickMarket/tree/local_app_with_database&#41;\&#41;)

[//]: # (+ Hosted application, web interface, with database \&#40;go to [branch]&#40;https://github.com/mateiungureanu/QuickMarket/tree/main&#41;\&#41;)

### Requirements

### Setup

### Pictures

### Romanian description

QuickMarket este o aplicatie de management al pietelor si tarabelor, conceputa pentru a facilita interactiunea intre vanzatori si cumparatori intr-un mediu virtual de piata.

#### Functionalitate

Aplicatia permite:
- Administrarea pietelor si tarabelor
- Gestionarea produselor si stocurilor
- Crearea si gestionarea listelor de cumparaturi
- Urmarirea istoricului de cumparaturi
- Interactiunea intre vanzatori si cumparatori

#### Pentru toti utilizatorii:
- Creeaza cont
- Login
- Logout

#### Pentru cumparatori:
- Vezi toate pietele
- Vezi toate tarabele
- Vezi produsele
- Adauga produs in lista de cumparaturi
- Marcheaza lista ca finalizata
- Vezi istoricul cumparaturilor

#### Pentru vanzatori:
- Adauga produs
- Sterge produs

#### Pentru administratori:
- Adauga o piata noua
- Vezi toti cumparatorii
- Vezi toti vanzatorii