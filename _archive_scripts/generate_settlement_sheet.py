import zlib
import struct
import os

def write_png(filename, width, height, pixels):
    raw_data = bytearray()
    for row in pixels:
        raw_data.append(0)
        for r, g, b, a in row:
            raw_data.extend([r, g, b, a])
    
    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)

    png = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    png += chunk(b'IHDR', ihdr)
    png += chunk(b'IDAT', zlib.compress(bytes(raw_data), level=9))
    png += chunk(b'IEND', b'')

    with open(filename, 'wb') as f:
        f.write(png)

def hex_to_rgba(hex_str, alpha=255):
    hex_str = hex_str.lstrip('#')
    return (int(hex_str[0:2], 16), int(hex_str[2:4], 16), int(hex_str[4:6], 16), alpha)

CLR = {
    'bg': hex_to_rgba('#0B1021'),
    'skin': hex_to_rgba('#F6C59E'),
    'white_bone': hex_to_rgba('#F3EFE0'),
    'stone_light': hex_to_rgba('#9AA0A6'),
    'stone_mid': hex_to_rgba('#5F6368'),
    'stone_dark': hex_to_rgba('#3C4043'),
    'wood_light': hex_to_rgba('#A66E38'),
    'wood_mid': hex_to_rgba('#78471D'),
    'wood_dark': hex_to_rgba('#4A2810'),
    'straw_light': hex_to_rgba('#ECC057'),
    'straw_mid': hex_to_rgba('#C5942B'),
    'straw_dark': hex_to_rgba('#8E6519'),
    'leaf_green': hex_to_rgba('#489436'),
    'leaf_dark': hex_to_rgba('#27611A'),
    'red_banner': hex_to_rgba('#9C2525'),
    'red_berry': hex_to_rgba('#D32F2F'),
    'purple_berry': hex_to_rgba('#7B1FA2'),
    'fire_yellow': hex_to_rgba('#FFF176'),
    'fire_orange': hex_to_rgba('#FB8C00'),
    'fire_red': hex_to_rgba('#E53935'),
}

def generate_settlement_sheet():
    w, h = 320, 180
    grid = [[CLR['bg'] for _ in range(w)] for _ in range(h)]
    
    # 1. Chieftain Longhouse in center top (x: 180, y: 10, w: 90, h: 70)
    cx, cy = 200, 15
    # Roof Straw
    for r in range(40):
        span = int(15 + r * 1.1)
        for c in range(-span, span):
            grid[cy + r][cx + c] = CLR['straw_light'] if (r + c) % 3 == 0 else CLR['straw_mid']
    # Animal skull on top
    for r in range(12):
        for c in range(-6, 7):
            grid[cy - 6 + r][cx + c] = CLR['white_bone']
    # Entrance
    for r in range(25):
        for c in range(-12, 13):
            grid[cy + 40 + r][cx + c] = CLR['wood_dark']
    # Red Banners
    for r in range(35):
        for c in range(10):
            grid[cy + 25 + r][cx - 30 + c] = CLR['red_banner']
            grid[cy + 25 + r][cx + 20 + c] = CLR['red_banner']
    # Torches
    for r in range(35):
        grid[cy + 30 + r][cx - 36] = CLR['wood_mid']
        grid[cy + 30 + r][cx + 36] = CLR['wood_mid']
    for r in range(8):
        for c in range(-3, 4):
            grid[cy + 24 + r][cx - 36 + c] = CLR['fire_orange']
            grid[cy + 24 + r][cx + 36 + c] = CLR['fire_orange']

    # 2. Straw Hut on left (x: 40, y: 20)
    hx, hy = 50, 25
    for r in range(35):
        span = int(10 + r * 0.9)
        for c in range(-span, span):
            grid[hy + r][hx + c] = CLR['straw_light'] if (r % 2 == 0) else CLR['straw_dark']
    # Leaves on top
    for r in range(6):
        for c in range(-8, 9):
            grid[hy - 4 + r][hx + c] = CLR['leaf_green']
    # Door
    for r in range(18):
        for c in range(-7, 8):
            grid[hy + 22 + r][hx + c] = CLR['wood_dark']

    # 3. Bone Hut (x: 120, y: 25)
    bx, by = 120, 25
    for r in range(35):
        span = int(12 + r * 0.8)
        for c in range(-span, span):
            grid[by + r][bx + c] = CLR['wood_dark']
    # White rib arcs
    for r in range(30):
        grid[by + r][bx - 14] = CLR['white_bone']
        grid[by + r][bx + 14] = CLR['white_bone']
        grid[by + r][bx - 7] = CLR['white_bone']
        grid[by + r][bx + 7] = CLR['white_bone']
    # Skull top
    for r in range(8):
        for c in range(-4, 5):
            grid[by - 3 + r][bx + c] = CLR['white_bone']

    # 4. Campfire (x: 45, y: 100)
    fx, fy = 45, 115
    for r in range(12):
        for c in range(-15, 16):
            if (c*c)/225.0 + (r*r)/144.0 <= 1.0:
                grid[fy + 10 + r][fx + c] = CLR['stone_mid']
    # Fire
    for r in range(20):
        span = int(max(0, 10 - r * 0.5))
        for c in range(-span, span + 1):
            grid[fy + 12 - r][fx + c] = CLR['fire_yellow'] if r < 8 else (CLR['fire_orange'] if r < 14 else CLR['fire_red'])

    # 5. Sun Monolith (x: 110, y: 95)
    mx, my = 110, 100
    for r in range(35):
        for c in range(-10, 11):
            grid[my + r][mx + c] = CLR['stone_light'] if (r + c) % 4 == 0 else CLR['stone_mid']
    # Sun glyph circle
    for r in range(-4, 5):
        for c in range(-4, 5):
            if r*r + c*c <= 16:
                grid[my + 14 + r][mx + c] = CLR['fire_yellow']
    # Tusks
    for r in range(25):
        grid[my + 10 + r][mx - 14] = CLR['white_bone']
        grid[my + 10 + r][mx + 14] = CLR['white_bone']

    # 6. Meat Drying Rack (x: 180, y: 100)
    rx, ry = 180, 100
    for r in range(30):
        grid[ry + r][rx - 15] = CLR['wood_mid']
        grid[ry + r][rx + 15] = CLR['wood_mid']
    for c in range(-16, 17):
        grid[ry + 5][rx + c] = CLR['wood_mid']
    # Hanging meat
    for r in range(16):
        for c in range(-3, 4):
            grid[ry + 7 + r][rx - 7 + c] = CLR['red_banner']
            grid[ry + 7 + r][rx + 7 + c] = CLR['red_banner']
    
    # 7. Berry Bushes (x: 240, 275, y: 105)
    for bx_pos, berry_col in [(240, CLR['red_berry']), (275, CLR['purple_berry'])]:
        for r in range(26):
            for c in range(-14, 15):
                if (c*c)/196.0 + (r*r)/676.0 <= 0.9:
                    grid[110 + r][bx_pos + c] = CLR['leaf_green'] if (r + c) % 3 != 0 else CLR['leaf_dark']
        # Berries
        for offset in [(-6, -4), (4, -6), (-3, 6), (5, 5), (0, 0)]:
            grid[120 + offset[1]][bx_pos + offset[0]] = berry_col
            grid[121 + offset[1]][bx_pos + offset[0]] = berry_col
            grid[120 + offset[1]][bx_pos + offset[0] + 1] = berry_col

    return grid

out_dir = "app/src/main/res/drawable"
write_png(f"{out_dir}/img_prehistoric_settlement.png", 320, 180, generate_settlement_sheet())
print("Settlement sheet generated successfully!")
