resource "aws_security_group" "alb" {
  name        = "${var.project_name}-alb-sg"
  description = "Allow public HTTP traffic to the Application Load Balancer"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-alb-sg"
    Project = var.project_name
  }
}

resource "aws_security_group" "app" {
  name        = "${var.project_name}-app-sg"
  description = "Security group for application EC2 instances"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-app-sg"
    Project = var.project_name
  }
}

resource "aws_security_group" "kafka" {
  name        = "${var.project_name}-kafka-sg"
  description = "Security group for Kafka EC2 instance"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-kafka-sg"
    Project = var.project_name
  }
}

resource "aws_security_group" "rds" {
  name        = "${var.project_name}-rds-sg"
  description = "Security group for RDS PostgreSQL"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-rds-sg"
    Project = var.project_name
  }
}

resource "aws_security_group" "ssh" {
  name        = "${var.project_name}-ssh-sg"
  description = "Temporary SSH access from my IP"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-ssh-sg"
    Project = var.project_name
  }
}

resource "aws_vpc_security_group_ingress_rule" "alb_http_from_internet" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow HTTP from internet"

  ip_protocol = "tcp"
  from_port   = 80
  to_port     = 80
  cidr_ipv4   = "0.0.0.0/0"
}

resource "aws_vpc_security_group_egress_rule" "alb_to_app_document_service" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow ALB to reach document-service"

  ip_protocol                  = "tcp"
  from_port                    = 8081
  to_port                      = 8081
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_egress_rule" "alb_to_app_processing_service" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow ALB to reach processing-service if needed"

  ip_protocol                  = "tcp"
  from_port                    = 8082
  to_port                      = 8082
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_egress_rule" "alb_to_app_export_job_service" {
  security_group_id = aws_security_group.alb.id
  description       = "Allow ALB to reach export-job-service if needed"

  ip_protocol                  = "tcp"
  from_port                    = 8083
  to_port                      = 8083
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_ingress_rule" "app_document_service_from_alb" {
  security_group_id = aws_security_group.app.id
  description       = "Allow ALB to access document-service"

  ip_protocol                  = "tcp"
  from_port                    = 8081
  to_port                      = 8081
  referenced_security_group_id = aws_security_group.alb.id
}

resource "aws_vpc_security_group_ingress_rule" "app_processing_service_from_alb" {
  security_group_id = aws_security_group.app.id
  description       = "Allow ALB to access processing-service if needed"

  ip_protocol                  = "tcp"
  from_port                    = 8082
  to_port                      = 8082
  referenced_security_group_id = aws_security_group.alb.id
}

resource "aws_vpc_security_group_ingress_rule" "app_export_job_service_from_alb" {
  security_group_id = aws_security_group.app.id
  description       = "Allow ALB to access export-job-service if needed"

  ip_protocol                  = "tcp"
  from_port                    = 8083
  to_port                      = 8083
  referenced_security_group_id = aws_security_group.alb.id
}

resource "aws_vpc_security_group_ingress_rule" "app_internal_http_8081" {
  security_group_id = aws_security_group.app.id
  description       = "Allow internal app communication on port 8081"

  ip_protocol                  = "tcp"
  from_port                    = 8081
  to_port                      = 8081
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_ingress_rule" "app_internal_http_8082" {
  security_group_id = aws_security_group.app.id
  description       = "Allow internal app communication on port 8082"

  ip_protocol                  = "tcp"
  from_port                    = 8082
  to_port                      = 8082
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_ingress_rule" "app_internal_http_8083" {
  security_group_id = aws_security_group.app.id
  description       = "Allow internal app communication on port 8083"

  ip_protocol                  = "tcp"
  from_port                    = 8083
  to_port                      = 8083
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_egress_rule" "app_all_egress" {
  security_group_id = aws_security_group.app.id
  description       = "Allow outbound traffic from app instances"

  ip_protocol = "-1"
  cidr_ipv4   = "0.0.0.0/0"
}

resource "aws_vpc_security_group_ingress_rule" "kafka_from_apps" {
  security_group_id = aws_security_group.kafka.id
  description       = "Allow application services to access Kafka broker"

  ip_protocol                  = "tcp"
  from_port                    = 9092
  to_port                      = 9092
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_egress_rule" "kafka_all_egress" {
  security_group_id = aws_security_group.kafka.id
  description       = "Allow outbound traffic from Kafka instance"

  ip_protocol = "-1"
  cidr_ipv4   = "0.0.0.0/0"
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_apps" {
  security_group_id = aws_security_group.rds.id
  description       = "Allow application services to access PostgreSQL RDS"

  ip_protocol                  = "tcp"
  from_port                    = 5432
  to_port                      = 5432
  referenced_security_group_id = aws_security_group.app.id
}

resource "aws_vpc_security_group_egress_rule" "rds_all_egress" {
  security_group_id = aws_security_group.rds.id
  description       = "Allow outbound traffic from RDS"

  ip_protocol = "-1"
  cidr_ipv4   = "0.0.0.0/0"
}

resource "aws_vpc_security_group_ingress_rule" "ssh_from_my_ip" {
  security_group_id = aws_security_group.ssh.id
  description       = "Allow SSH from my public IP"

  ip_protocol = "tcp"
  from_port   = 22
  to_port     = 22
  cidr_ipv4   = var.my_ip_cidr
}

resource "aws_vpc_security_group_egress_rule" "ssh_all_egress" {
  security_group_id = aws_security_group.ssh.id
  description       = "Allow outbound traffic from SSH-managed instances"

  ip_protocol = "-1"
  cidr_ipv4   = "0.0.0.0/0"
}