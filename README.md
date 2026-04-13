# Digital Wallet System

## Project Overview
This Digital Wallet System is a microservices-based application built using Java to facilitate secure and efficient digital transactions. It allows users to perform various financial operations, manage their accounts, and conduct transactions seamlessly.

## Architecture
The architecture of the Digital Wallet System is based on a microservices approach. Each service is independently deployable and can be developed, maintained, and scaled independently. Here’s a brief overview of the main components:

- **User Service**: Manages user accounts and authentication.
- **Transaction Service**: Handles all financial transactions and records.
- **Wallet Service**: Manages wallet creation, balance checks, and fund transfers.
- **Notification Service**: Sends notifications for transaction updates and account activity.

The services communicate with each other using REST APIs and Kafka for message brokering, ensuring loose coupling and scalability.

## Features
- **User Authentication**: Secure login and registration process.
- **Fund Transfers**: Easy transfer of funds between users.
- **Transaction History**: View transaction history for better tracking.
- **Balance Inquiry**: Check wallet balance at any time.
- **Notifications**: Get real-time notifications for transactions and activities.

## Installation
### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- MySQL or PostgreSQL (for database)

### Steps to Install
1. Clone the repository:
   ```bash
   git clone https://github.com/AbhayBharti96/DigitalWalletSystem.git
   ```
2. Navigate to the project directory:
   ```bash
   cd DigitalWalletSystem
   ```
3. Install dependencies using Maven:
   ```bash
   mvn clean install
   ```
4. Set up the database:
   - Create a database named `digital_wallet`
   - Import the schema from `schema.sql` located in the `src/main/resources` directory.

5. Configure application properties:
   - Update the `application.properties` file with your database credentials and other settings.

6. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Usage
- After starting the application, access the user service at:
  - **User Service**: `http://localhost:8080/users`
  - **Transaction Service**: `http://localhost:8081/transactions`
  - **Wallet Service**: `http://localhost:8082/wallet`
  - **Notification Service**: `http://localhost:8083/notifications`

- You can test the endpoints using tools like Postman or Curl for API operations.

## Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
Special thanks to contributors and libraries that made this project possible.