# Keeper UI - ZooKeeper HTTP Proxy

**⚠️ Experimental Project**

Keeper UI is an HTTP proxy server for Apache ZooKeeper that provides RESTful APIs to interact with ZooKeeper clusters. This project serves as the backend for the ZooKeeper UI hosted at https://zk.ankitsultana.com.

## Features

- **Multi-Instance Support**: Connect to multiple ZooKeeper clusters simultaneously
- **RESTful APIs**: Complete CRUD operations for ZooKeeper nodes
- **Node Statistics**: Retrieve detailed ZooKeeper node metadata
- **Ephemeral & Persistent Nodes**: Support for both node types
- **Configurable**: YAML-based configuration for easy deployment

## API Endpoints

All endpoints are prefixed with `/{instance}` where `instance` is the configured ZooKeeper instance name.

- `GET /{instance}/ls?path=/` - List children of a node
- `GET /{instance}/get?path=/` - Get node data, statistics, and children
- `POST /{instance}/create` - Create a new node
- `DELETE /{instance}/delete?path=/&version=-1` - Delete a node
- `POST /{instance}/set?path=/&version=-1` - Update node data

## Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Access to a ZooKeeper cluster

### Building

```bash
mvn clean package
```

### Running

#### With default configuration
```bash
java -jar target/keeper-ui-1.0-SNAPSHOT.jar
```

#### With custom configuration
```bash
java -jar target/keeper-ui-1.0-SNAPSHOT.jar /path/to/your/application.yml
```

The server will start on port 12345 by default.

## Configuration

Create an `application.yml` file to configure the server and ZooKeeper instances:

```yaml
server:
  port: 12345

zookeeper:
  dev:
    host: localhost:9180
    session-timeout: 5000
  prod:
    host: prod-zk1:2181,prod-zk2:2181,prod-zk3:2181
    session-timeout: 10000
  local:
    host: localhost:2181
    session-timeout: 3000
```

### Configuration Options

#### Server Configuration
- `server.port`: HTTP server port (default: 12345)

#### ZooKeeper Instance Configuration
Each ZooKeeper instance supports:
- `host`: ZooKeeper connection string (single host or comma-separated cluster)
- `session-timeout`: Session timeout in milliseconds

### Example Usage

```bash
# List root children for 'dev' instance
curl "http://localhost:12345/dev/ls?path=/"

# Get node data for 'prod' instance
curl "http://localhost:12345/prod/get?path=/config"

# Create a new node
curl -X POST "http://localhost:12345/dev/create" \
  -H "Content-Type: application/json" \
  -d '{"path": "/test", "data": "hello world", "isEphemeral": false}'

# Delete a node
curl -X DELETE "http://localhost:12345/dev/delete?path=/test&version=-1"
```

## API Response Format

All API responses return JSON:

**Success responses:**
```json
{
  "children": ["node1", "node2"],
  "data": "node content",
  "stat": {
    "czxid": 123,
    "mzxid": 456,
    "version": 1,
    ...
  }
}
```

**Error responses:**
```json
{
  "message": "Error description"
}
```

## Development

### Project Structure

```
src/main/java/com/ankitsultana/keeper/ui/
├── KeeperUIApplication.java    # Main Spring Boot application
├── ZookeeperFacade.java       # ZooKeeper client wrapper
├── ZookeeperFactory.java      # Multi-instance connection factory
└── AppConfig.java             # Configuration binding
```

### Running in Development

```bash
mvn spring-boot:run
```

## Credits

- **UI**: Built with ❤️ by [lovable.dev](https://lovable.dev)
- **HTTP Proxy**: Built with [Claude](https://claude.ai) AI Assistant
- **Framework**: Spring Boot + Apache ZooKeeper Client

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*This is an experimental project created for educational and demonstration purposes.*