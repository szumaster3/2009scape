#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Updates `shops.json` config by setting stock amounts
equal to `AMOUNT` when they are 0.
"""

import json
import re
import sys
import shutil
from datetime import datetime
from pathlib import Path

AMOUNT = 10

STOCK_PATTERN = re.compile(r"\{(\d+),(\d+),(\d+)\}")
BASE_DIR = Path(__file__).resolve().parent
ROOT_DIR = BASE_DIR.parents[3]
SHOP_FILE = ROOT_DIR / "2009Scape/Server/data/configs/shops.json"


def update_stock(stock: str) -> str:
    def repl(match):
        item_id, amount, price = match.groups()
        return f"{{{item_id},{AMOUNT},{price}}}" if amount == "0" else match.group(0)

    return STOCK_PATTERN.sub(repl, stock)


def make_backup(file: Path) -> Path:
    date = datetime.now().strftime("%d_%m_%Y")

    backup_dir = BASE_DIR / "backup"
    backup_dir.mkdir(exist_ok=True)

    base_name = f"shops_backup_{date}.json"
    backup = backup_dir / base_name

    counter = 1
    while backup.exists():
        backup = backup_dir / f"shops_backup_{date}_{counter}.json"
        counter += 1

    shutil.copy2(file, backup)
    return backup


def main() -> None:
    if not SHOP_FILE.exists():
        sys.exit(f"Error: file not found={SHOP_FILE}\nUsage: python shops_stock.py")

    with SHOP_FILE.open(encoding="utf-8") as f:
        data = json.load(f)

    backup = make_backup(SHOP_FILE)

    modified = 0
    for shop in data:
        original_stock = shop.get("stock", "")
        updated_stock = update_stock(original_stock)

        if updated_stock != original_stock:
            modified += 1
            shop["stock"] = updated_stock

    with SHOP_FILE.open("w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)

    GOLD = "\033[33;1m"
    RESET = "\033[0m"
    print(f"")
    print(f"Created backup: {GOLD}{backup.name}{RESET} dir= {GOLD}{backup}{RESET}")
    print(f"")
    print(f"Update completed | modified shops: {GOLD}{modified}{RESET}")
    print(f"")


if __name__ == "__main__":
    main()
